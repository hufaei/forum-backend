package com.lisan.forumbackend.model.dto.announcements;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建通告表请求
 *
 * @author lisan
 *
 */
@Data
public class AnnouncementsAddRequest implements Serializable {

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