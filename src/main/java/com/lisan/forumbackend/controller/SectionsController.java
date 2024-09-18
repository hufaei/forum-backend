package com.lisan.forumbackend.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.lisan.forumbackend.common.BaseResponse;
import com.lisan.forumbackend.common.DeleteRequest;
import com.lisan.forumbackend.common.ErrorCode;
import com.lisan.forumbackend.common.ResultUtils;
import com.lisan.forumbackend.exception.BusinessException;
import com.lisan.forumbackend.exception.ThrowUtils;
import com.lisan.forumbackend.model.dto.sections.SectionsAddRequest;
import com.lisan.forumbackend.model.entity.Sections;
import com.lisan.forumbackend.model.vo.SectionsVO;
import com.lisan.forumbackend.service.SectionsService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 板块表接口
 * @author ぼつち
 */
@RestController
@RequestMapping("/sections")
@Slf4j
public class SectionsController {

    @Resource
    private SectionsService sectionsService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private RedissonClient redissonClient;
    // Redis 缓存键
    private static final String SECTION_KEY = "sections";


    /**
     * 创建板块表
     * 仅管理员可用
     * @param sectionsAddRequest 板块请求数据
     * @param request 网络请求
     * @return Long
     */
    @PostMapping("/add")
    public BaseResponse<Long> addSections(@RequestBody SectionsAddRequest sectionsAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(sectionsAddRequest == null, ErrorCode.PARAMS_ERROR);

        StpUtil.checkLogin();
        StpUtil.checkRole("ADMIN");

        // 使用 Redisson 获取分布式锁
        RLock lock = redissonClient.getLock(SECTION_KEY + ":lock");
        try {
            if (lock.tryLock(10, 5, TimeUnit.SECONDS)) {

                // 实体类和 DTO 进行转换
                Sections sections = new Sections();
                BeanUtils.copyProperties(sectionsAddRequest, sections);

                // 数据校验
                sectionsService.validSections(sections);

                // 保存数据
                boolean result = sectionsService.save(sections);
                ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

                // 成功添加后，删除缓存
                redisTemplate.delete(SECTION_KEY);

                long newSectionsId = sections.getId();
                return ResultUtils.success(newSectionsId);
            } else {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "系统繁忙，请稍后再试");
            }
        } catch (InterruptedException e) {
            log.error("获取锁失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取锁失败");
        } finally {
            // 释放锁
            lock.unlock();
        }
    }



    /**
     * 删除板块表（已完成--不可随便调用）
     * 仅管理员可用
     * @param deleteRequest 通用删除请求
     * @param request 网络请求
     * @return Boolean
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSections(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        StpUtil.checkLogin();
        StpUtil.checkRole("ADMIN");

        // 使用 Redisson 获取分布式锁
        RLock lock = redissonClient.getLock(SECTION_KEY + ":lock");
        try {
            if (lock.tryLock(10, 5, TimeUnit.SECONDS)) {

                long id = deleteRequest.getId();
                Sections oldSections = sectionsService.getById(id);
                ThrowUtils.throwIf(oldSections == null, ErrorCode.NOT_FOUND_ERROR);

                // 删除数据库数据
                boolean result = sectionsService.removeById(id);
                ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

                // 删除缓存
                redisTemplate.delete(SECTION_KEY);

                return ResultUtils.success(true);
            } else {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "系统繁忙，请稍后再试");
            }
        } catch (InterruptedException e) {
            log.error("获取锁失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取锁失败");
        } finally {
            // 释放锁
            lock.unlock();
        }
    }


    /**
     * 根据板块表列表（封装类）
     * @return 板块视图列表
     */
    @GetMapping("/all")
    public BaseResponse<List<SectionsVO>> getAllSections() {

        List<SectionsVO> cachedSections = (List<SectionsVO>) redisTemplate.opsForValue().get(SECTION_KEY);

        if (cachedSections != null) {
            return ResultUtils.success(cachedSections);
        }

        RLock lock = redissonClient.getLock(SECTION_KEY + ":lock");
        try {
            if (lock.tryLock(10, 5, TimeUnit.SECONDS)) {
                // 双重缓存检查
                List<SectionsVO> cachedSectionTwice = (List<SectionsVO>) redisTemplate.opsForValue().get(SECTION_KEY);

                if (cachedSectionTwice != null) {
                    return ResultUtils.success(cachedSectionTwice);
                }
                List<SectionsVO> sectionsVOList = sectionsService.getAllSections();

                redisTemplate.opsForValue().set(SECTION_KEY, sectionsVOList);

                return ResultUtils.success(sectionsVOList);
            } else {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "系统繁忙，请稍后再试");
            }
        } catch (InterruptedException e) {
            log.error("获取锁失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取锁失败");
        } finally {
            lock.unlock();
        }
    }


}
