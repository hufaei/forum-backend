package com.lisan.forumbackend.model.dto.comments;

import lombok.Data;

import java.io.Serializable;

/**
 * 编辑评论表请求
 *
 * @author lisan
 *
 */
@Data
public class CommentsEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;


    private static final long serialVersionUID = 1L;
}