package com.lisan.forumbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
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
     *
     * @param comments
     */
    void validComments(Comments comments);

    boolean removeById(Serializable id);

    List<CommentsVO> getCommentsByTopicId(Long topicId);
//
//    /**
//     * 获取查询条件
//     *
//     * @param commentsQueryRequest
//     * @return
//     */
//    QueryWrapper<Comments> getQueryWrapper(CommentsQueryRequest commentsQueryRequest);
//    
//    /**
//     * 获取评论表封装
//     *
//     * @param comments
//     * @param request
//     * @return
//     */
//    CommentsVO getCommentsVO(Comments comments, HttpServletRequest request);
//
//    /**
//     * 分页获取评论表封装
//     *
//     * @param commentsPage
//     * @param request
//     * @return
//     */
//    Page<CommentsVO> getCommentsVOPage(Page<Comments> commentsPage, HttpServletRequest request);

}
