package com.lisan.forumbackend.model.dto.topics;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新话题表请求
 *
 * @author lisan
 *
 */
@Data
public class TopicsUpdateRequest implements Serializable {

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