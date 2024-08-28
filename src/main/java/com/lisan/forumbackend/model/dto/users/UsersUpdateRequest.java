package com.lisan.forumbackend.model.dto.users;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新用户表请求
 *
 * @author lisan
 *
 */
@Data
public class UsersUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 自我简介
     */
    private String self_intro;

    private static final long serialVersionUID = 1L;
}