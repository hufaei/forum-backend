package com.lisan.forumbackend.model.dto.users;


import lombok.Data;
import java.io.Serializable;


/**
 * 创建用户表请求
 *
 * @author lisan
 *
 */
@Data
public class UsersAddRequest implements Serializable {
    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 密码
     */
    private String password;

//    /**
//     * 确认密码
//     */
//    private String confirm;

    /**
     * 邮箱
     */
    private String email;

//    /**
//     * 权限——用户
//     */
//    private String role = "USER";

    private static final long serialVersionUID = 1L;
}