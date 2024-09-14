package com.lisan.forumbackend.common;

import lombok.Data;

/**
 * 分页请求
 * @author ぼつち
 * 
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private int current = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认升序）
     * 降序——descend
     */
    private String sortOrder = "ascend";
}
