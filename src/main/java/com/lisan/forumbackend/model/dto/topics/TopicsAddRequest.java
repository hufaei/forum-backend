package com.lisan.forumbackend.model.dto.topics;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建话题表请求
 * @author lisan
 *
 */
@Data
public class TopicsAddRequest implements Serializable {


    /**
     * 话题创建者id
     */
    private Long userId;
    /**
     * 内容
     */
    private String content;

    /**
     * 板块id
     */
    private Long sectionId;

    /**
     * 图片附件
     */
    private List<String> image;



    private static final long serialVersionUID = 1L;
}