package com.lisan.forumbackend.model.dto.users;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询用户表请求
 *
 * @author lisan
 *
 */

@Data
public class UsersLoginRequest implements Serializable {

    /**
     * 用户名/邮箱
     */
    private String usernameOrEmail;

    /**
     * 密码
     */
    private String password;


    private static final long serialVersionUID = 1L;
}