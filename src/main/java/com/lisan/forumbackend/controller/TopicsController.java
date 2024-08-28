package com.lisan.forumbackend.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.lisan.forumbackend.common.BaseResponse;
import com.lisan.forumbackend.common.DeleteRequest;
import com.lisan.forumbackend.common.ErrorCode;
import com.lisan.forumbackend.common.ResultUtils;
import com.lisan.forumbackend.exception.BusinessException;
import com.lisan.forumbackend.exception.ThrowUtils;
import com.lisan.forumbackend.model.dto.topics.TopicsAddRequest;
import com.lisan.forumbackend.model.entity.Topics;
import com.lisan.forumbackend.model.vo.TopicsVO;
import com.lisan.forumbackend.service.CommentsService;
import com.lisan.forumbackend.service.TopicsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 话题表接口
 * @author lisan
 */
@RestController
@RequestMapping("/topics")
@Slf4j
public class TopicsController {

    @Resource
    private TopicsService topicsService;
    @Resource
    private CommentsService commentService;
    

    // region 增删改查

    /**
     * 创建话题表
     *
     * @param topicsAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTopics(@RequestBody TopicsAddRequest topicsAddRequest, HttpServletRequest request) {
        // 确保用户已登录
        StpUtil.checkLogin();

        ThrowUtils.throwIf(topicsAddRequest == null, ErrorCode.PARAMS_ERROR);

        // 实体类和 DTO 进行转换
        Topics topics = new Topics();
        BeanUtils.copyProperties(topicsAddRequest, topics);

        // 数据校验
        topicsService.validTopics(topics);

        // 设置当前登录用户的 ID
        long currentUserId = StpUtil.getLoginIdAsLong();
        topics.setUserId(currentUserId);

        // 写入数据库
        boolean result = topicsService.save(topics);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 返回新写入的数据 id
        long newTopicsId = topics.getId();
        return ResultUtils.success(newTopicsId);
    }

    /**
     * 删除话题表
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTopics(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        // 确保用户已登录
        StpUtil.checkLogin();

        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long id = deleteRequest.getId();

        // 判断是否存在
        Topics oldTopics = topicsService.getById(id);
        ThrowUtils.throwIf(oldTopics == null, ErrorCode.NOT_FOUND_ERROR);

        // 获取当前用户的 ID
        long currentUserId = StpUtil.getLoginIdAsLong();

        // 根据用户角色判断权限
        if (!StpUtil.hasRole("ADMIN") && oldTopics.getUserId() != currentUserId) {
            ThrowUtils.throwIf(true, ErrorCode.NO_AUTH_ERROR);
        }

        // 操作数据库
        boolean result = topicsService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }


     /**
     * 根据 section_id 获取话题表（封装类）
     * @param sectionId
     * @return
     */
     @GetMapping("/get/TopicsVo/{sectionId}")
     public BaseResponse<List<TopicsVO>> getTopicsVOById(@PathVariable("sectionId") Long sectionId, HttpServletRequest request) {
         ThrowUtils.throwIf(sectionId == null || sectionId <= 0, ErrorCode.PARAMS_ERROR);
         // 查询数据库
         List<TopicsVO> topicsVOList = topicsService.getTopicsVOBySectionId(sectionId);
         return ResultUtils.success(topicsVOList);
     }

    /**
     * 根据 userId 获取话题表（封装类）
     * @param userId
     * @param request
     * @return
     */
    @GetMapping("/get/TopicsVoByUserId/{userId}")
    public BaseResponse<List<TopicsVO>> getTopicsVOByUserId(@PathVariable("userId") Long userId, HttpServletRequest request) {
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        List<TopicsVO> topicsVOList = topicsService.getTopicsVOByUserId(userId);
        return ResultUtils.success(topicsVOList);
    }


}
