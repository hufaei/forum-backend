package com.lisan.forumbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lisan.forumbackend.model.dto.comments.CommentPagesRequest;
import com.lisan.forumbackend.model.entity.Comments;
import com.lisan.forumbackend.model.vo.CommentsVO;

import java.io.Serializable;
import java.util.List;

/**
 * 评论表服务
 *
 * @author lisan
 *
 */
public interface CommentsService extends IService<Comments> {

    /**
     * 校验数据
     * @param comments 评论实体类
     */
    void validComments(Comments comments);

    boolean removeById(Serializable id);

    List<CommentsVO> getCommentsByTopicId(CommentPagesRequest commentPagesRequest);

}
