package com.lisan.forumbackend.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.lisan.forumbackend.common.BaseResponse;
import com.lisan.forumbackend.common.DeleteRequest;
import com.lisan.forumbackend.common.ErrorCode;
import com.lisan.forumbackend.common.ResultUtils;
import com.lisan.forumbackend.exception.BusinessException;
import com.lisan.forumbackend.exception.ThrowUtils;
import com.lisan.forumbackend.model.dto.comments.CommentsAddRequest;
import com.lisan.forumbackend.model.entity.Comments;
import com.lisan.forumbackend.model.vo.CommentsVO;
import com.lisan.forumbackend.service.CommentsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.lisan.forumbackend.model.dto.comments.CommentPagesRequest;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 评论表接口
 * @author lisan
 */
@RestController
@RequestMapping("/comments")
@Slf4j
public class CommentsController {

    @Resource
    private CommentsService commentsService;

//     region 增删改查

    /**
     * 创建评论表
     *
     * @param commentsAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addComments(@RequestBody CommentsAddRequest commentsAddRequest, HttpServletRequest request) {
        StpUtil.checkLogin();
        ThrowUtils.throwIf(commentsAddRequest == null, ErrorCode.PARAMS_ERROR);

        // 将实体类和 DTO 进行转换
        Comments comments = new Comments();
        BeanUtils.copyProperties(commentsAddRequest, comments);
        // 数据校验
        commentsService.validComments(comments);
        // 直接传入登录用户id
        comments.setUserId(StpUtil.getLoginIdAsLong());
        // 写入数据库
        boolean result = commentsService.save(comments);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newCommentsId = comments.getId();
        return ResultUtils.success(newCommentsId);
    }

    /**
     * 删除评论表
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteComments(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        StpUtil.checkLogin();

        long id = deleteRequest.getId();
        // 判断是否存在
        Comments oldComments = commentsService.getById(id);
        ThrowUtils.throwIf(oldComments == null, ErrorCode.NOT_FOUND_ERROR);

        long currentUserId = StpUtil.getLoginIdAsLong();

        long commentUserId = oldComments.getUserId();
        // 鉴权
        if (!StpUtil.hasRole("ADMIN") && commentUserId != currentUserId) {
            ThrowUtils.throwIf(true, ErrorCode.NO_AUTH_ERROR);
        }

        // 操作数据库
        boolean result = commentsService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }


    @GetMapping("/get/CommentsVo/{topicId}/{current}")
    public BaseResponse<List<CommentsVO>> getCommentsByTopicId(@PathVariable("topicId") Long topicId, @PathVariable("current") int current, HttpServletRequest request) {
        ThrowUtils.throwIf(topicId == null || topicId <= 0, ErrorCode.PARAMS_ERROR);
        CommentPagesRequest commentPagesRequest = new CommentPagesRequest();
        commentPagesRequest.setTopicId(topicId);
        commentPagesRequest.setCurrent(current);
        // 查询数据库
        List<CommentsVO> commentVOList = commentsService.getCommentsByTopicId(commentPagesRequest);
        return ResultUtils.success(commentVOList);
    }

}
