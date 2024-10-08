package com.lisan.forumbackend.model.dto.sections;

import lombok.Data;

import java.io.Serializable;

/**
 * 编辑板块表请求
 *
 * @author lisna
 *
 */
@Data
public class SectionsEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    private static final long serialVersionUID = 1L;
}