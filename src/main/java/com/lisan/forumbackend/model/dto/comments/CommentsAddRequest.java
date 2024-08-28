package com.lisan.forumbackend.model.dto.comments;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建评论表请求
 *
 * @author lisan
 *
 */
@Data
public class CommentsAddRequest implements Serializable {

    /**
     * 评论话题id
     */
    private Long topicId;


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