package com.lisan.forumbackend.model.dto.topics;

import lombok.Data;

import java.io.Serializable;

/**
 * 编辑话题表请求
 *
 * @author lisan
 *
 */
@Data
public class TopicsEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;


    /**
     * 内容
     */
    private String content;


    private static final long serialVersionUID = 1L;
}