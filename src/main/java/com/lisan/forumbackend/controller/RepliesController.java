package com.lisan.forumbackend.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.lisan.forumbackend.common.BaseResponse;
import com.lisan.forumbackend.common.DeleteRequest;
import com.lisan.forumbackend.common.ErrorCode;
import com.lisan.forumbackend.common.ResultUtils;
import com.lisan.forumbackend.exception.BusinessException;
import com.lisan.forumbackend.exception.ThrowUtils;
import com.lisan.forumbackend.model.dto.replies.RepliesAddRequest;
import com.lisan.forumbackend.model.entity.Replies;
import com.lisan.forumbackend.model.vo.RepliesVO;
import com.lisan.forumbackend.service.RepliesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 回复表接口
 * @author lisan
 */
@RestController
@RequestMapping("/replies")
@Slf4j
public class RepliesController {

    @Resource
    private RepliesService repliesService;

    /**
     * 创建回复表
     *
     * @param repliesAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addReplies(@RequestBody RepliesAddRequest repliesAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(repliesAddRequest == null, ErrorCode.PARAMS_ERROR);
        // 实体类和 DTO 进行转换
        Replies replies = new Replies();
        BeanUtils.copyProperties(repliesAddRequest, replies);
        // 数据校验
        repliesService.validReplies(replies);
        // 写入登录用户id
        StpUtil.checkLogin();
        replies.setUserId(StpUtil.getLoginIdAsLong());
        // 写入数据库
        boolean result = repliesService.save(replies);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newRepliesId = replies.getId();
        return ResultUtils.success(newRepliesId);
    }

    /**
     * 删除回复表
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteReplies(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        StpUtil.checkLogin();

        // 获取当前用户的 ID
        long currentUserId = StpUtil.getLoginIdAsLong();

        long id = deleteRequest.getId();
        // 判断是否存在
        Replies oldReplies = repliesService.getById(id);
        ThrowUtils.throwIf(oldReplies == null, ErrorCode.NOT_FOUND_ERROR);

        // 根据用户角色判断权限
        if (!StpUtil.hasRole("ADMIN") && oldReplies.getUserId() != currentUserId) {
            ThrowUtils.throwIf(true, ErrorCode.NO_AUTH_ERROR);
        }

        // 操作数据库
        boolean result = repliesService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @GetMapping("/get/RepliesVo/{commentId}")
    public BaseResponse<List<RepliesVO>> getRepliesByCommentId(@PathVariable("commentId") Long commentId, HttpServletRequest request) {
        ThrowUtils.throwIf(commentId == null || commentId <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        List<RepliesVO> repliesVOList = repliesService.getRepliesByCommentId(commentId);
        return ResultUtils.success(repliesVOList);
    }

}
