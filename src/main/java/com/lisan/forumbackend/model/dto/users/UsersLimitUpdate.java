package com.lisan.forumbackend.model.dto.users;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新用户表请求
 * 修改密码/修改邮箱
 * @author lisan
 *
 */
@Data
public class UsersLimitUpdate implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 密码--仅调用改密码时候传此参数
     */
    private String password;

    /**
     * 邮箱--另外的修改邮箱调用
     */
    private String email;



    private static final long serialVersionUID = 1L;
}