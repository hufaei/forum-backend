package com.lisan.forumbackend.model.dto.announcements;

import lombok.Data;

import java.io.Serializable;

/**
 * 编辑通告表请求
 *
 * @author lisan
 *
 */
@Data
public class AnnouncementsEditRequest implements Serializable {

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