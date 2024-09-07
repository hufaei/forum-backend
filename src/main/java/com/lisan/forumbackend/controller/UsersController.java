package com.lisan.forumbackend.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lisan.forumbackend.common.BaseResponse;
import com.lisan.forumbackend.common.DeleteRequest;
import com.lisan.forumbackend.common.ErrorCode;
import com.lisan.forumbackend.common.ResultUtils;
import com.lisan.forumbackend.exception.BusinessException;
import com.lisan.forumbackend.exception.ThrowUtils;
import com.lisan.forumbackend.model.dto.users.UsersAddRequest;
import com.lisan.forumbackend.model.dto.users.UsersLoginRequest;
import com.lisan.forumbackend.model.dto.users.UsersPagesRequest;
import com.lisan.forumbackend.model.dto.users.UsersUpdateRequest;
import com.lisan.forumbackend.model.entity.Users;
import com.lisan.forumbackend.model.enums.TuccEnum;
import com.lisan.forumbackend.model.vo.UsersVO;
import com.lisan.forumbackend.service.ImageService;
import com.lisan.forumbackend.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

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
    private ImageService imageService;

    // region 增删改查

    /**
     * 创建用户表
     *
     * @param usersAddRequest
     * @return
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
     * 用户和管理员可用
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUsers(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        StpUtil.checkLogin();
//        long id = StpUtil.getLoginIdAsLong(); // 获取当前会话账号id, 并转化为`long`类型
        long id = deleteRequest.getId();
        // 判断是否存在
        Users oldUsers = usersService.getById(id);
        ThrowUtils.throwIf(oldUsers == null, ErrorCode.NOT_FOUND_ERROR);
//        // 仅本人或管理员可删除
//        if (!oldUsers.getUserId().equals(Users.getId()) && !usersService.isAdmin(request)) {
//            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
//        }
        // 操作数据库
        boolean result = usersService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

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
            // 框架登录生成token
            StpUtil.login(user.getId());
            /**
             * StpUtil.getTokenInfo()--获取token
             * StpUtil.isLogin()--是否登录
             * StpUtil.logout()--登出
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
     * @param userId
     * @return
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
     * @param usersUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> editUsers(@RequestBody UsersUpdateRequest usersUpdateRequest, HttpServletRequest request) {

        if (usersUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        StpUtil.checkLogin();

        //传入updaterequest时不指定id，根据token直接设置为用户登录id
        usersUpdateRequest.setId(StpUtil.getLoginIdAsLong());

        Users users = new Users();
        BeanUtils.copyProperties(usersUpdateRequest, users);
        // 数据校验
        Users updataUser = usersService.updateValid(users);
        // 判断是否存在
        long id = updataUser.getId();
        Users loginUsers = usersService.getById(id);
        ThrowUtils.throwIf(loginUsers == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = usersService.updateById(updataUser);
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
     * @param file
     * @return
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


}
