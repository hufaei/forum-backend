package com.lisan.forumbackend.model.dto.comments;

import com.lisan.forumbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 根据传入topicId按页数查询所有评论
 *
 * @author lisan
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommentPagesRequest extends PageRequest implements Serializable {

    /**
     * 话题id
     */
    private Long topicId;



    private static final long serialVersionUID = 1L;
}