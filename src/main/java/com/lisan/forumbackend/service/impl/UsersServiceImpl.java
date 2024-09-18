package com.lisan.forumbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lisan.forumbackend.common.ErrorCode;
import com.lisan.forumbackend.exception.BusinessException;
import com.lisan.forumbackend.exception.ThrowUtils;
import com.lisan.forumbackend.mapper.UsersMapper;
import com.lisan.forumbackend.model.dto.users.UsersLoginRequest;
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
    private TopicsService topicsService;
    @Autowired
    private FollowsService followsService;


    /**
     * 校验数据
     * @param users 用户实体
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

//    @Override
//    public QueryWrapper<Users> getQueryWrapper(UsersPagesRequest usersPagesRequest) {
//        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
//        if (usersPagesRequest == null) {
//            return queryWrapper;
//        }
//        // 从对象中取值
//        Long id = usersPagesRequest.getId();
//        String sortField = usersPagesRequest.getSortField();
//        String sortOrder = usersPagesRequest.getSortOrder();

//
//        return queryWrapper;
//    }

    /**
     * 获取用户表封装
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

        return true;
    }


}
