package com.lisan.forumbackend.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lisan.forumbackend.common.BaseResponse;
import com.lisan.forumbackend.common.DeleteRequest;
import com.lisan.forumbackend.common.ErrorCode;
import com.lisan.forumbackend.common.ResultUtils;
import com.lisan.forumbackend.exception.BusinessException;
import com.lisan.forumbackend.exception.ThrowUtils;
import com.lisan.forumbackend.model.dto.announcements.AnnouncementsAddRequest;
import com.lisan.forumbackend.model.dto.announcements.AnnouncementsQueryRequest;
import com.lisan.forumbackend.model.dto.announcements.AnnouncementsUpdateRequest;
import com.lisan.forumbackend.model.entity.Announcements;
import com.lisan.forumbackend.model.vo.AnnouncementsVO;
import com.lisan.forumbackend.service.AnnouncementsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 通告表接口
 * @author lisan
 */
@RestController
@RequestMapping("/announcements")
@Slf4j
public class AnnouncementsController {

    @Resource
    private AnnouncementsService announcementsService;
    @Autowired
    private RedisTemplate redisTemplate;


    // Redis 缓存键
    private static final String ANNOUNCEMENTS_CACHE_KEY = "announcement:latest";
    // Redis 缓存过期时间：15天
    private static final long CACHE_EXPIRATION = 15L * 24 * 60 * 60; // 15天，单位为秒

    /**
     * 创建通告表
     * 仅管理员身份可用
     * @param announcementsAddRequest
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addAnnouncements(@RequestBody AnnouncementsAddRequest announcementsAddRequest) {

        StpUtil.checkLogin();
        StpUtil.checkRole("ADMIN");
        ThrowUtils.throwIf(announcementsAddRequest == null, ErrorCode.PARAMS_ERROR);

        // DTO 转换 实体类
        Announcements announcements = new Announcements();
        BeanUtils.copyProperties(announcementsAddRequest, announcements);

        // 数据校验
        announcementsService.validAnnouncements(announcements);

        // 写入数据库
        announcements.setIsDelete(0);
        boolean result = announcementsService.save(announcements);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 成功添加后，删除缓存
        redisTemplate.delete(ANNOUNCEMENTS_CACHE_KEY);

        // 返回新写入的数据 id
        long newAnnouncementsId = announcements.getId();
        return ResultUtils.success(newAnnouncementsId);
    }

    /**
     * 删除通告表
     * 仅管理员可删除
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteAnnouncements(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {

        StpUtil.checkLogin();
        StpUtil.checkRole("ADMIN");

        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long id = deleteRequest.getId();

        // 判断是否存在
        Announcements oldAnnouncements = announcementsService.getById(id);
        ThrowUtils.throwIf(oldAnnouncements == null, ErrorCode.NOT_FOUND_ERROR);

        // 操作数据库
        boolean result = announcementsService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        redisTemplate.delete(ANNOUNCEMENTS_CACHE_KEY);
        return ResultUtils.success(true);
    }

    /**
     * 更新通告表（仅管理员可用）
     * @param announcementsUpdateRequest
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateAnnouncements(@RequestBody AnnouncementsUpdateRequest announcementsUpdateRequest) {

        StpUtil.checkLogin();
        StpUtil.checkRole("ADMIN");

        if (announcementsUpdateRequest == null || announcementsUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 实体类和 DTO 进行转换
        Announcements announcements = new Announcements();
        BeanUtils.copyProperties(announcementsUpdateRequest, announcements);

        // 数据校验
        announcementsService.validAnnouncements(announcements);

        // 判断是否存在
        long id = announcementsUpdateRequest.getId();
        Announcements oldAnnouncements = announcementsService.getById(id);
        ThrowUtils.throwIf(oldAnnouncements == null, ErrorCode.NOT_FOUND_ERROR);

        // 操作数据库
        boolean result = announcementsService.updateById(announcements);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        redisTemplate.delete(ANNOUNCEMENTS_CACHE_KEY);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取通告表视图
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<AnnouncementsVO> getAnnouncementsVOById(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Announcements announcements = announcementsService.getById(id);
        ThrowUtils.throwIf(announcements == null, ErrorCode.NOT_FOUND_ERROR);

        // 获取封装类
        return ResultUtils.success(announcementsService.getAnnouncementsVO(announcements, request));
    }

    /**
     * 分页获取通告表列表
     *
     * @param announcementsQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<AnnouncementsVO>> listAnnouncementsByPage(@RequestBody AnnouncementsQueryRequest announcementsQueryRequest, HttpServletRequest request) {
        // 从 Redis 获取缓存数据
        List<AnnouncementsVO> cachedAnnouncements = (List<AnnouncementsVO>) redisTemplate.opsForValue().get(ANNOUNCEMENTS_CACHE_KEY);

        if (cachedAnnouncements != null) {
            // 将缓存数据转换为分页对象并返回
            Page<AnnouncementsVO> cachedPage = new Page<>(1, cachedAnnouncements.size(), cachedAnnouncements.size());
            cachedPage.setRecords(cachedAnnouncements);
            return ResultUtils.success(cachedPage);
        }

        long current = 1;
        long size = 3;

        // 判断是否为无条件查询
        boolean isQueryAll = announcementsQueryRequest.getId() == null
                && StringUtils.isBlank(announcementsQueryRequest.getSearchText())
                && StringUtils.isBlank(announcementsQueryRequest.getSortField());

        // 查询数据库
        Page<Announcements> announcementsPage;
        if (isQueryAll) {
            announcementsPage = announcementsService.page(new Page<>(current, size));
        } else {
            announcementsPage = announcementsService.page(new Page<>(current, size), announcementsService.getQueryWrapper(announcementsQueryRequest));
        }

        // 判断查询结果是否为空
        if (announcementsPage == null || announcementsPage.getTotal() == 0) {
            return ResultUtils.success(new Page<>());
        }

        // 转换为 AnnouncementsVO
        List<AnnouncementsVO> announcementsVOList = announcementsPage.getRecords().stream()
                .map(announcement -> announcementsService.getAnnouncementsVO(announcement, request))
                .collect(Collectors.toList());

        // 更新到Redis缓存
        redisTemplate.opsForValue().set(ANNOUNCEMENTS_CACHE_KEY, announcementsVOList, CACHE_EXPIRATION, TimeUnit.SECONDS);

        // 构建返回的分页对象
        Page<AnnouncementsVO> announcementsVOPage = new Page<>(current, size, announcementsPage.getTotal());
        announcementsVOPage.setRecords(announcementsVOList);

        return ResultUtils.success(announcementsVOPage);
    }

}
