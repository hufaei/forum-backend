package com.lisan.forumbackend.model.dto.topics;

import com.lisan.forumbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 根据传入板块Id按页数查询所有话题
 *
 * @author lisan
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TopicPagesRequest extends PageRequest implements Serializable {

    /**
     * 板块id
     */
    private Long sectionId;



    private static final long serialVersionUID = 1L;
}