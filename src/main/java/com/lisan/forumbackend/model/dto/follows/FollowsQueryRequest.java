package com.lisan.forumbackend.model.dto.follows;

import com.lisan.forumbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询关注表请求
 *
 * @author lisan
 *
 */
@Data
public class FollowsQueryRequest implements Serializable {

    /**
     * 关注者id
     */
    private Long follower_id;

    /**
     * 被关注者id
     */
    private Long followee_id;


    private static final long serialVersionUID = 1L;
}