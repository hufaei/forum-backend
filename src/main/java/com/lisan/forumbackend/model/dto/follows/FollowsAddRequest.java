package com.lisan.forumbackend.model.dto.follows;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建关注表请求
 *
 * @author lisan
 *
 */
@Data
public class FollowsAddRequest implements Serializable {


    /**
     * 关注者id
     */
    private Long followerId;

    /**
     * 被关注着用户id
     */
    private Long followeeId;

    private static final long serialVersionUID = 1L;
}