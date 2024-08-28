package com.lisan.forumbackend.model.dto.sections;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建板块表请求
 *
 * @author lisna
 *
 */
@Data
public class SectionsAddRequest implements Serializable {

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