package com.lisan.forumbackend.model.dto.sections;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新板块表请求
 *
 * @author lisan
 *
 */
@Data
public class SectionsUpdateRequest implements Serializable {

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