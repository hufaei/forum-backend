package com.lisan.forumbackend.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lisan.forumbackend.annotation.AuthCheck;
import com.lisan.forumbackend.common.BaseResponse;
import com.lisan.forumbackend.common.DeleteRequest;
import com.lisan.forumbackend.common.ErrorCode;
import com.lisan.forumbackend.common.ResultUtils;
import com.lisan.forumbackend.constant.UserConstant;
import com.lisan.forumbackend.exception.BusinessException;
import com.lisan.forumbackend.exception.ThrowUtils;
import com.lisan.forumbackend.model.dto.follows.FollowsAddRequest;
import com.lisan.forumbackend.model.dto.follows.FollowsEditRequest;
import com.lisan.forumbackend.model.dto.follows.FollowsQueryRequest;
import com.lisan.forumbackend.model.dto.follows.FollowsUpdateRequest;
import com.lisan.forumbackend.model.entity.Follows;
import com.lisan.forumbackend.model.vo.FollowsVO;
import com.lisan.forumbackend.service.FollowsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 关注表接口
 * @author lisan
 */
@RestController
@RequestMapping("/follows")
@Slf4j
public class FollowsController {

    @Resource
    private FollowsService followsService;
    

    // region 增删改查

    @PostMapping("/toggle")
    public BaseResponse<Boolean> toggleFollows(@RequestBody FollowsAddRequest followsAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(followsAddRequest == null, ErrorCode.PARAMS_ERROR);

        // 实体类和 DTO 进行转换
        Follows follows = new Follows();
        BeanUtils.copyProperties(followsAddRequest, follows);

        // 数据校验
        followsService.validFollows(follows);

        StpUtil.checkLogin();
        // todo 应该校验是否传入的请求id和token_id是否一致————总有人不怀好意

        Long followerId = StpUtil.getLoginIdAsLong();

        Long followeeId = followsAddRequest.getFolloweeId();

        QueryWrapper<Follows> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("follower_id", followerId).eq("followee_id", followeeId);
        Follows existingFollows = followsService.getOne(queryWrapper);

        boolean result;
        if (existingFollows == null) {
            follows.setFollowerId(followerId);
            result = followsService.save(follows);
        } else {
            // 如果存在关注关系，则执行取消关注操作
            result = followsService.removeById(existingFollows.getId());
        }

        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(result);
    }

    /**
     * 根据 用户id 获取关注表（封装类）
     *
     * @param userId
     * @return
     */
    @GetMapping("/get/vo/{userId}")
    public BaseResponse<List<FollowsVO>> getFollowsVOById(@PathVariable("userId") Long userId, HttpServletRequest request) {
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        List<FollowsVO> followsVO = followsService.getFollowsVO(userId);

        return ResultUtils.success(followsVO);
    }
    @PostMapping("/checkMutualFollow")
    public BaseResponse<Boolean> checkMutualFollow(@RequestBody FollowsQueryRequest followsQueryRequest) {
        StpUtil.checkLogin();

        Long followerid = followsQueryRequest.getFollower_id();
        Long followeeid = followsQueryRequest.getFollowee_id();
        ThrowUtils.throwIf(followerid == null || followeeid == null || followerid <= 0 || followeeid <= 0, ErrorCode.PARAMS_ERROR);

        boolean isMutualFollow = followsService.isMutualFollow(followerid, followeeid);

        return ResultUtils.success(isMutualFollow);
    }

}
