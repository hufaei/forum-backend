package com.lisan.forumbackend.model.vo;

import cn.hutool.json.JSONUtil;
import com.lisan.forumbackend.model.entity.Users;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户表视图
 *
 * @author lisan
 *
 */
@Data
public class UsersVO implements Serializable {

    /**
     * 用户id
     */
    private Long id;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 账号
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 自我介绍
     */
    private String self_intro;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 封装类转对象
     *
     * @param usersVO
     * @return Users
     */
    public static Users voToObj(UsersVO usersVO) {
        if (usersVO == null) {
            return null;
        }
        Users users = new Users();
        BeanUtils.copyProperties(usersVO, users);
        return users;
    }

    /**
     * 对象转封装类
     *
     * @param users
     * @return UsersVO
     */
    public static UsersVO objToVo(Users users) {
        if (users == null) {
            return null;
        }
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(users, usersVO);
        return usersVO;
    }
}
