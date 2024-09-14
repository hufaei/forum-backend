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
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
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
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private RedissonClient redissonClient;

    // Redis 缓存键
    private static final String ANNOUNCEMENTS_CACHE_KEY = "announcement:latest";
    // Redisson 锁键
    private static final String ANNOUNCEMENTS_LOCK_KEY = "lock:announcements";
    // 过期时间
    private static final long EXPIRATION = 15L * 24 * 60 * 60; // 15天，单位为秒

    @PostMapping("/add")
    public BaseResponse<Long> addAnnouncements(@RequestBody AnnouncementsAddRequest announcementsAddRequest) {

        StpUtil.checkLogin();
        StpUtil.checkRole("ADMIN");
        ThrowUtils.throwIf(announcementsAddRequest == null, ErrorCode.PARAMS_ERROR);

        // 同一个锁名--互斥
        RLock lock = redissonClient.getLock(ANNOUNCEMENTS_LOCK_KEY);
        try {
            // 获取锁，超时时间为 10 秒，锁自动释放时间为 5 秒
            if (lock.tryLock(10, 5, TimeUnit.SECONDS)) {

                // DTO 转换 实体类
                Announcements announcements = new Announcements();
                BeanUtils.copyProperties(announcementsAddRequest, announcements);

                // 数据校验
                announcementsService.validAnnouncements(announcements);

                // 写入数据库
                announcements.setIsDelete(0);
                boolean result = announcementsService.save(announcements);
                ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

                // 缓存Aside写法
                redisTemplate.delete(ANNOUNCEMENTS_CACHE_KEY);

                // 返回新写入的数据 id
                long newAnnouncementsId = announcements.getId();
                return ResultUtils.success(newAnnouncementsId);
            } else {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "系统繁忙，请稍后再试");
            }
        } catch (InterruptedException e) {
            log.error("获取锁失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "系统繁忙，请稍后再试");
        } finally {
            lock.unlock();
        }
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteAnnouncements(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {

        StpUtil.checkLogin();
        StpUtil.checkRole("ADMIN");

        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        RLock lock = redissonClient.getLock(ANNOUNCEMENTS_LOCK_KEY);
        try {
            if (lock.tryLock(10, 5, TimeUnit.SECONDS)) {

                long id = deleteRequest.getId();

                // 判断是否存在
                Announcements oldAnnouncements = announcementsService.getById(id);
                ThrowUtils.throwIf(oldAnnouncements == null, ErrorCode.NOT_FOUND_ERROR);

                // 操作数据库
                boolean result = announcementsService.removeById(id);
                ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

                // 删除成功后，删除缓存
                redisTemplate.delete(ANNOUNCEMENTS_CACHE_KEY);

                return ResultUtils.success(true);
            } else {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "系统繁忙，请稍后再试");
            }
        } catch (InterruptedException e) {
            log.error("获取锁失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "系统繁忙，请稍后再试");
        } finally {
            lock.unlock();
        }
    }

    /**
     * 更新通告表（仅管理员可用--大概不会用）
     * @param announcementsUpdateRequest 通告表请求数据
     * @return Boolean
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
     * @author ぼつち
     * @param id 评论id
     * @return AnnouncementsVO
     */
    @GetMapping("/get/vo")
    public BaseResponse<AnnouncementsVO> getAnnouncementsVOById(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Announcements announcements = announcementsService.getById(id);
        ThrowUtils.throwIf(announcements == null, ErrorCode.NOT_FOUND_ERROR);

        // 获取封装类
        return ResultUtils.success(AnnouncementsVO.objToVo(announcements));
    }

    /**
     * @author ぼつち
     * 分页查询最新三条数据
     * @param announcementsQueryRequest 通告查询请求数据结构
     * &#064;description  使用锁的同时，优先读取缓存，更新同时才介入锁，最多阻塞一个进程而获取到缓存
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<AnnouncementsVO>> listAnnouncementsByPage(@RequestBody AnnouncementsQueryRequest announcementsQueryRequest) {

        List<AnnouncementsVO> cachedAnnouncements = (List<AnnouncementsVO>) redisTemplate.opsForValue().get(ANNOUNCEMENTS_CACHE_KEY);
        if (cachedAnnouncements != null) {
            Page<AnnouncementsVO> cachedPage = new Page<>(1, cachedAnnouncements.size(), cachedAnnouncements.size());
            cachedPage.setRecords(cachedAnnouncements);
            return ResultUtils.success(cachedPage);
        }

        // 获取分布式锁，确保在修改数据时不能读取
        RLock lock = redissonClient.getLock(ANNOUNCEMENTS_LOCK_KEY);
        try {
            if (lock.tryLock(10, 5, TimeUnit.SECONDS)) {

                long current = 1;
                long size = 3;

                // 判断是否为无条件查询
                boolean isQueryAll = announcementsQueryRequest.getId() == null
                        && StringUtils.isBlank(announcementsQueryRequest.getSearchText())
                        && StringUtils.isBlank(announcementsQueryRequest.getSortField());

                Page<Announcements> announcementsPage;
                if (isQueryAll) {
                    announcementsPage = announcementsService.page(new Page<>(current, size));
                } else {
                    announcementsPage = announcementsService.page(new Page<>(current, size), announcementsService.getQueryWrapper(announcementsQueryRequest));
                }

                // 转换为 AnnouncementsVO
                List<AnnouncementsVO> announcementsVOList = announcementsPage.getRecords().stream()
                        .map(AnnouncementsVO::objToVo)
                        .collect(Collectors.toList());

                // 更新缓存
                redisTemplate.opsForValue().set(ANNOUNCEMENTS_CACHE_KEY, announcementsVOList, EXPIRATION, TimeUnit.SECONDS);

                Page<AnnouncementsVO> announcementsVOPage = new Page<>(current, size, announcementsPage.getTotal());
                announcementsVOPage.setRecords(announcementsVOList);

                return ResultUtils.success(announcementsVOPage);
            } else {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "系统繁忙，请稍后再试");
            }
        } catch (InterruptedException e) {
            log.error("获取锁失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "系统繁忙，请稍后再试");
        } finally {
            // 释放锁
            lock.unlock();
        }
    }



}
