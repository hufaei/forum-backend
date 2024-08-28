package com.lisan.forumbackend.model.dto.replies;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建回复表请求
 *
 * @author lisan
 *
 */
@Data
public class RepliesAddRequest implements Serializable {

    /**
     * 回复评论id
     */
    private Long commentId;


    /**
     * 评论用户id
     */
    private Long userId;

    /**
     * 内容
     */
    private String content;


    private static final long serialVersionUID = 1L;
}