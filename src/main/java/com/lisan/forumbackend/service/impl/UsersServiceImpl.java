package com.lisan.forumbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lisan.forumbackend.common.ErrorCode;
import com.lisan.forumbackend.exception.BusinessException;
import com.lisan.forumbackend.exception.ThrowUtils;
import com.lisan.forumbackend.mapper.UsersMapper;
import com.lisan.forumbackend.model.dto.users.UsersLoginRequest;
import com.lisan.forumbackend.model.dto.users.UsersPagesRequest;
import com.lisan.forumbackend.model.entity.Follows;
import com.lisan.forumbackend.model.entity.Topics;
import com.lisan.forumbackend.model.entity.Users;
import com.lisan.forumbackend.model.vo.UsersVO;
import com.lisan.forumbackend.service.FollowsService;
import com.lisan.forumbackend.service.TopicsService;
import com.lisan.forumbackend.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户表服务实现
 *
 * @author lisan
 *
 */
@Service
@Slf4j
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {

    @Resource
    private UsersMapper usersMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TopicsService topicsService;
    @Autowired
    private FollowsService followsService;


    /**
     * 校验数据
     *
     * @param users
     */
    @Override
    public Users validUsers(Users users) {
        // 检查用户对象是否为空
        ThrowUtils.throwIf(users == null, ErrorCode.PARAMS_ERROR);

        // 从对象中取值
        String username = users.getUsername();
        String email = users.getEmail();
        String nickname = users.getNickname();
        String pwd = users.getPassword();

        // 校验规则
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(nickname) && StringUtils.isNotBlank(pwd)) {
            ThrowUtils.throwIf(username.length() > 30, ErrorCode.PARAMS_ERROR, "账户过长");
            ThrowUtils.throwIf(nickname.length() > 10, ErrorCode.PARAMS_ERROR, "昵称过长");
            ThrowUtils.throwIf(pwd.length() > 20, ErrorCode.PARAMS_ERROR, "密码过长");
        }
        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Users::getEmail, email).or().eq(Users::getUsername, username);
        Users existingUser = usersMapper.selectOne(queryWrapper);
        ThrowUtils.throwIf(existingUser != null, ErrorCode.PARAMS_ERROR, "该用户已存在");
        // 生成盐值并加密密码
        String salt = generateSalt();
        String encryptedPwd = encryptPassword(pwd, salt);

        // 创建并返回新的用户对象
        Users newUser = new Users();
        newUser.setEmail(email);
        newUser.setUsername(username);
        newUser.setNickname(nickname);
        newUser.setRole("USER");
        newUser.setPassword(encryptedPwd);
        newUser.setSalt(salt);

        return newUser;
    }
    @Override
    public Users updateValid(Users users) {
        // 检查用户对象是否为空
        ThrowUtils.throwIf(users == null, ErrorCode.PARAMS_ERROR);

        // 创建新的用户对象
        Users newUser = new Users();

        // 根据传入的非空属性进行设置
        if (users.getNickname() != null) {
            newUser.setNickname(users.getNickname());
        }
        if (users.getAvatar() != null) {
            newUser.setAvatar(users.getAvatar());
        }
        if (users.getSelf_intro() != null) {
            newUser.setSelf_intro(users.getSelf_intro());
        }
        // todo 获取当前登录用户的ID并且设置新的用户ID为已登录用户的ID


        newUser.setId(users.getId());

        return newUser;
    }
    @Override
    public Users login(UsersLoginRequest request) {
        String usernameOrEmail = request.getUsernameOrEmail();
        String password = request.getPassword();

        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Users::getUsername, usernameOrEmail)
                .or()
                .eq(Users::getEmail, usernameOrEmail);
        Users user = usersMapper.selectOne(queryWrapper);
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "用户不存在");

        // 校验密码
        String encryptedPwd = encryptPassword(password, user.getSalt());
        ThrowUtils.throwIf(!encryptedPwd.equals(user.getPassword()), ErrorCode.PARAMS_ERROR, "用户名或密码错误");

        return user;
    }
    private String generateSalt() {
        return java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    private String encryptPassword(String password, String salt) {
        // 使用MD5和盐值加密密码的逻辑
        return DigestUtils.md5Hex(password + salt);
    }
    /**
     * 获取查询条件
     *
     * @param usersPagesRequest
     * @return
     */
    @Override
    public QueryWrapper<Users> getQueryWrapper(UsersPagesRequest usersPagesRequest) {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        if (usersPagesRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = usersPagesRequest.getId();
        String sortField = usersPagesRequest.getSortField();
        String sortOrder = usersPagesRequest.getSortOrder();
        // todo 补充需要的查询条件

        return queryWrapper;
    }

    /**
     * 获取用户表封装
     *
     * @param users
     * @param request
     * @return
     */
    @Override
    public UsersVO getUsersVO(Users users, HttpServletRequest request) {
        // 对象转封装类
        UsersVO usersVO = UsersVO.objToVo(users);

        System.out.println(users);
        System.out.println(usersVO);
        Long id = usersVO.getId();

        String username = usersVO.getUsername();
        String email = usersVO.getEmail();
        Date createAt = usersVO.getCreatedAt();

        // 判空
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR, "不存在的信息");
        ThrowUtils.throwIf(StringUtils.isEmpty(username), ErrorCode.PARAMS_ERROR, "未读取到信息");
        ThrowUtils.throwIf(StringUtils.isEmpty(email), ErrorCode.PARAMS_ERROR, "未读取到信息");
        ThrowUtils.throwIf(createAt == null, ErrorCode.PARAMS_ERROR, "未读取到信息");



        return usersVO;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
        if (id == null || (Long) id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 处理关注表中的相关记录
        followsService.remove(new QueryWrapper<Follows>().eq("follower_id", id));
        followsService.remove(new QueryWrapper<Follows>().eq("followee_id", id));
        // 删除用户相关的信息记录
        List<Topics> topics = topicsService.list(new QueryWrapper<Topics>().eq("user_id", id));
        for (Topics topic : topics) {
            topicsService.removeById(topic.getId());
        }
        // 删除用户记录
        boolean userRemoved = super.removeById(id);
        if (!userRemoved) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        redisTemplate.opsForValue().set("deleted_user:" + id, id);

        return true;
    }


//
//    /**
//     * 分页获取用户表封装
//     *
//     * @param usersPage
//     * @param request
//     * @return
//     */
//    @Override
//    public Page<UsersVO> getUsersVOPage(Page<Users> usersPage, HttpServletRequest request) {
//        List<Users> usersList = usersPage.getRecords();
//        Page<UsersVO> usersVOPage = new Page<>(usersPage.getCurrent(), usersPage.getSize(), usersPage.getTotal());
//        if (CollUtil.isEmpty(usersList)) {
//            return usersVOPage;
//        }
//        // 对象列表 => 封装对象列表
//        List<UsersVO> usersVOList = usersList.stream().map(users -> {
//            return UsersVO.objToVo(users);
//        }).collect(Collectors.toList());
//
//        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
//        // region 可选
//        // 1. 关联存在性
////        Set<Long> userIdSet = usersList.stream().map(Users::getId).collect(Collectors.toSet());
////        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
////                .collect(Collectors.groupingBy(User::getId));
//        // 2. 已登录，获取用户点赞、收藏状态
////        User loginUser = userService.getLoginUserPermitNull(request);
////       usersFavourList.forEach(usersFavour -> usersIdHasFavourMap.put(usersFavour.getUsersId(), true));
////        // 填充信息
////        usersVOList.forEach(usersVO -> {
////            Long userId = usersVO.getUserId();
////            User user = null;
////            if (userIdUserListMap.containsKey(userId)) {
////                user = userIdUserListMap.get(userId).get(0);
////            }
////            usersVO.setUser(userService.getUserVO(user));
////            usersVO.setHasThumb(usersIdHasThumbMap.getOrDefault(usersVO.getId(), false));
////            usersVO.setHasFavour(usersIdHasFavourMap.getOrDefault(usersVO.getId(), false));
////        });
////        // endregion
////
////        usersVOPage.setRecords(usersVOList);
//        return usersVOPage;
//    }

}
