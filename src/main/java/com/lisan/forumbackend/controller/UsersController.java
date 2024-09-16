package com.lisan.forumbackend.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lisan.forumbackend.common.BaseResponse;
import com.lisan.forumbackend.common.ErrorCode;
import com.lisan.forumbackend.common.ResultUtils;
import com.lisan.forumbackend.exception.BusinessException;
import com.lisan.forumbackend.exception.ThrowUtils;
import com.lisan.forumbackend.model.dto.users.UsersAddRequest;
import com.lisan.forumbackend.model.dto.users.UsersLoginRequest;
import com.lisan.forumbackend.model.dto.users.UsersUpdateRequest;
import com.lisan.forumbackend.model.entity.Users;
import com.lisan.forumbackend.model.enums.TuccEnum;
import com.lisan.forumbackend.model.vo.UsersVO;
import com.lisan.forumbackend.service.ImageService;
import com.lisan.forumbackend.service.RedisEmailCodeService;
import com.lisan.forumbackend.service.TopicsService;
import com.lisan.forumbackend.service.UsersService;
import com.lisan.forumbackend.utils.EmailUtils;
import com.lisan.forumbackend.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户表接口
 * @author lisan
 */
@RestController
@RequestMapping("/users")
@Slf4j

public class UsersController {

    @Resource
    private UsersService usersService;
    @Resource
    private TopicsService topicsService;
    @Resource
    private ImageService imageService;
    @Resource
    private RedisEmailCodeService redisEmailCodeService;
    

    /**
     * 创建用户表
     * @param usersAddRequest 用户添加请求
     * @return Long
     */
    @PostMapping("/add")
    public BaseResponse<Long> addUsers(@RequestBody UsersAddRequest usersAddRequest) {
        ThrowUtils.throwIf(usersAddRequest == null, ErrorCode.PARAMS_ERROR);
//        String pwd = usersAddRequest.getPassword();
//        String confirm = usersAddRequest.getConfirm();
//        System.out.println(pwd+"/"+confirm);
//        System.out.println(usersAddRequest);
//        ThrowUtils.throwIf(!StringUtils.equals(pwd, confirm), ErrorCode.PARAMS_ERROR);
        // 实体类和 DTO 进行转换
        Users users = new Users();
        BeanUtils.copyProperties(usersAddRequest, users);
        // 数据校验
        Users tuser = usersService.validUsers(users);

        // 写入数据库
        boolean result = usersService.save(tuser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        Long newUsersId = users.getId();
        return ResultUtils.success(newUsersId);
    }
    /**
     * 用户注销
     * 用户可用
     * @param request 网络请求
     * @return 是否注销成功
     */
    @PostMapping("/self/delete")
    public BaseResponse<Boolean> deleteUsers(HttpServletRequest request) {

        StpUtil.checkLogin();
        long id = StpUtil.getLoginIdAsLong(); // 获取当前会话账号id,
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在
        Users oldUsers = usersService.getById(id);
        ThrowUtils.throwIf(oldUsers == null, ErrorCode.NOT_FOUND_ERROR);
        // 先退出登录
        StpUtil.logout();
        // 再封禁账号,单位：s(-1:永久）
        StpUtil.disable(id, -1);
        // 操作数据库
        boolean result = usersService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }
    /**
     * 登录方法
     * @param request 登录请求数据
     * @param response 网络请求
     * @return 用户视图
     */
    @PostMapping("/login")
    public BaseResponse<UsersVO> login(@RequestBody UsersLoginRequest request, HttpServletResponse response) {
        // 检查 request 对象是否为 null
        if (request == null ||
                StringUtils.isBlank(request.getUsernameOrEmail()) ||
                StringUtils.isBlank(request.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //获取Vo层
        Users user = usersService.login(request);
        if(user!=null){
            UsersVO usersVO = UsersVO.objToVo(user);

            // 校验指定账号是否已被封禁，如果被封禁则抛出异常 `DisableServiceException`
            StpUtil.checkDisable(user.getId());
            // 框架登录生成token
            StpUtil.login(user.getId());
            /*
              StpUtil.getTokenInfo()--获取token
              StpUtil.isLogin()--是否登录
              StpUtil.logout()--登出
             */
            // 返回用户信息和 token
            String token = StpUtil.getTokenValue();

            return ResultUtils.success(usersVO,token);
        }else {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR);
        }

    }
    /**
     * 根据 id 获取用户表（封装类）
     * 点击用户查询其信息
     * @param userId 用户id
     * @return UsersVO
     */
    @GetMapping("/get/vo/{userId}")
    public BaseResponse<UsersVO> getUsersVOById(@PathVariable("userId") Long userId, HttpServletRequest request) {
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Users users = usersService.getById(userId);
        ThrowUtils.throwIf(users == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(usersService.getUsersVO(users, request));
    }

    /**
     * 修改用户信息（给用户使用）
     *
     * @param usersUpdateRequest 用户更新数据结构
     * @param request 网络请求
     * @return Boolean
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> editUsers(@RequestBody UsersUpdateRequest usersUpdateRequest, HttpServletRequest request) {

        if (usersUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        StpUtil.checkLogin();
        //传入update-request时不指定id，根据token直接设置为用户登录id
        usersUpdateRequest.setId(StpUtil.getLoginIdAsLong());

        Users users = new Users();
        BeanUtils.copyProperties(usersUpdateRequest, users);
        // 数据校验
        Users updateUser = usersService.updateValid(users);
        // 判断是否存在
        long id = updateUser.getId();
        Users loginUsers = usersService.getById(id);
        ThrowUtils.throwIf(loginUsers == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = usersService.updateById(updateUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }
    /**
     * 用户登出
     * @return BaseResponse<Boolean>
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> logout() {
        try {
            // 1. 调用 Sa-Token 框架的登出方法，清除当前会话的 satoken
            StpUtil.logout();

            // 2. 返回登出成功的响应
            return ResultUtils.success(true);
        } catch (Exception e) {
            log.error("登出失败:", e);
            // 3. 如果登出失败，返回操作错误的响应
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "登出失败");
        }
    }

    /**
     * 用户上传头像（基于图床）
     * @param file 文件
     * @return Boolean
     */
    @PostMapping("/updateAvatar")
    public BaseResponse<Boolean> uploadImage(@RequestParam("file") MultipartFile file) {
        StpUtil.checkLogin();  // 验证用户是否登录

        try {
            String imageUrl = imageService.uploadImage(file, TuccEnum.AVATAR_ID);

            // 获取当前登录用户的 ID
            Long userId = StpUtil.getLoginIdAsLong();

            // 获取用户实体
            Users user = usersService.getById(userId);
            ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);

            // 更新用户头像 URL
            user.setAvatar(imageUrl);
            boolean result = usersService.updateById(user);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

            return ResultUtils.success(true,imageUrl);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "上传失败");
        }
    }
    /**
     * 分页获取发布话题最多的用户
     * @param current 当前页
     * @param size 每页显示的数据条数
     * @return BaseResponse 分页结果
     */
    @GetMapping("/top-users")
    public BaseResponse<List<Map<String, Object>>> getTopUsersByTopicCount(
            @RequestParam(value = "current", defaultValue = "1") int current,
            @RequestParam(value = "size", defaultValue = "5") int size) {

        List<Map<String, Object>> pageResult = topicsService.getTopUsersByTopicCount(current, size)
                                                            .getRecords();
        return ResultUtils.success(pageResult);
    }



    /**
     * 管理员封禁用户接口
     * @param userId 用户ID
     * @param duration 封禁时长，单位为秒（-1表示永久封禁）
     * @return 是否成功封禁
     */
    @PostMapping("/ban/{userId}")
    public BaseResponse<Boolean> banUser(@PathVariable("userId") Long userId,
                                         @RequestParam(value = "duration", defaultValue = "-1") long duration) {
        // 检查是否为管理员权限
        StpUtil.checkRole("ADMIN");

        // 检查用户是否存在
        Users user = usersService.getById(userId);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);

        // 先踢下线
        StpUtil.kickout(userId);
        // 执行封禁，单位为秒（-1 表示永久封禁）
        StpUtil.disable(userId, duration);

        return ResultUtils.success(true);
    }
    /**
     * 查询所有在线用户的会话信息
     * 仅管理员可用
     * @return 登录用户的视图和状态
     */
    @GetMapping("/all/online")
    public BaseResponse<List<Map<String, Object>>> getAllOnlineSessions() {
        // 检查是否为管理员权限
        StpUtil.checkRole("ADMIN");

        // 获取所有在线的 token 会话信息
        List<String> tokenList = StpUtil.searchTokenValue("", 0, -1,true); // 搜索在线token，分页设置为1000
        List<Map<String, Object>> onlineUsers = new ArrayList<>();

        for (String tokenEntry  : tokenList) {
            // 提取实际的 token
            String tokenValue = tokenEntry.replace("satoken:login:token:", "");
            // 根据 token 获取用户的详细信息
            Map<String, Object> sessionInfo = new HashMap<>();
            Object  loginIdObj = StpUtil.getLoginIdByToken(tokenValue);
            System.out.println(loginIdObj);

            // 如果 loginIdObj 为 null，说明 token 已失效，跳过该 token
            if (loginIdObj == null) {
                // 删除无效 token
                StpUtil.logoutByTokenValue(tokenValue);
                continue;  // 跳过失效的 token
            }

            // 将 loginId 转换为 Long，如果是 String 则先转换为 Long
            Long userId;
            if (loginIdObj instanceof String) {
                userId = Long.parseLong((String) loginIdObj);
            } else if (loginIdObj instanceof Long) {
                userId = (Long) loginIdObj;
            } else {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "不支持的 loginId 类型");
            }
            // 获取用户信息
            Users user = usersService.getById(userId);
            if (user != null) {
                // 获取用户状态和登录状态
                sessionInfo.put("userId", user.getId());
                sessionInfo.put("username", user.getUsername());
                sessionInfo.put("isLogin", StpUtil.isLogin(user.getId()));
                sessionInfo.put("token", tokenValue);
                sessionInfo.put("loginTime", StpUtil.getSessionByLoginId(userId).getCreateTime());  // 获取会话创建时间

                // 添加到结果列表
                onlineUsers.add(sessionInfo);
            }
        }

        return ResultUtils.success(onlineUsers);
    }
//    @Scheduled(cron = "0 0 * * * ?")  // 每小时执行一次
//    public void cleanInvalidTokens() {
//        List<String> tokenList = StpUtil.searchTokenValue("", 0, 1000, true);
//        for (String tokenEntry : tokenList) {
//            String tokenValue = tokenEntry.replace("satoken:login:token:", "");
//            if (StpUtil.getLoginIdByToken(tokenValue) == null) {
//                StpUtil.logoutByTokenValue(tokenValue);  // 清理无效 token
//            }
//        }
//    }
    @PostMapping("/sendEmail/{email}")
    public BaseResponse<Boolean> sendEmail(@PathVariable("email") String email) throws MessagingException {
        Integer authCode = ValidateCodeUtils.generateValidateCode(4);
        EmailUtils.sendEmail(email, String.valueOf(authCode));

        // 存储验证码到Redis，5分钟过期
        redisEmailCodeService.saveAuthCode(email, String.valueOf(authCode));
        return ResultUtils.success(true);
    }
    @PostMapping("/verifyCode")
    public BaseResponse<Boolean> verifyCode(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String code = requestBody.get("code");

        // 从Redis中获取验证码
        String redisCode = redisEmailCodeService.getAuthCode(email);
        if (redisCode == null) {
            return ResultUtils.error(ErrorCode.INVALIDECODE_ERROR);
        }

        // 验证验证码是否正确
        if (!redisCode.equals(code)) {
            // 验证后删除验证码，防止重复使用
            redisEmailCodeService.deleteAuthCode(email);
            return ResultUtils.error(ErrorCode.CODE_ERROR);
        }
        // 验证后删除验证码，防止重复使用
        redisEmailCodeService.deleteAuthCode(email);


        return ResultUtils.success(true);
    }
    @PostMapping("/verifyEmail/{email}")
    public BaseResponse<Boolean> verifyEmail(@PathVariable("email") String email) {
         // 检查邮箱是否存在
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        Users user = usersService.getOne(queryWrapper);
        if (user == null) {
            return ResultUtils.error(ErrorCode.EMAIL_ERROR);
        }

        return ResultUtils.success(true);
    }
    @PostMapping("/changePassword")
    public BaseResponse<Boolean> changePwd(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("email");
        String password = requestBody.get("password");
        // 检查邮箱是否存在
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", username);
        Users user = usersService.getOne(queryWrapper);
        if (user == null) {
            return ResultUtils.error(ErrorCode.EMAIL_ERROR);
        }
        // 每次改密码应该换盐值？
        String salt = user.getSalt();
        String encryptedPwd = DigestUtils.md5Hex(password + salt);
        user.setPassword(encryptedPwd);

        usersService.updateById(user);
        return ResultUtils.success(true);
    }
}
