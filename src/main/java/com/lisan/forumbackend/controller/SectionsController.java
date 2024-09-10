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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 板块表接口
 * @author lisan
 */
@RestController
@RequestMapping("/sections")
@Slf4j
public class SectionsController {

    @Resource
    private SectionsService sectionsService;
    @Autowired
    private RedisTemplate redisTemplate;

    // Redis 缓存键
    private static final String SECTION_KEY = "sections";


    /**
     * 创建板块表
     * 仅管理员可用
     * @param sectionsAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addSections(@RequestBody SectionsAddRequest sectionsAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(sectionsAddRequest == null, ErrorCode.PARAMS_ERROR);

        StpUtil.checkLogin();

        StpUtil.checkRole("ADMIN");

        // 实体类和 DTO 进行转换
        Sections sections = new Sections();
        BeanUtils.copyProperties(sectionsAddRequest, sections);

        // 数据校验
        sectionsService.validSections(sections);

        boolean result = sectionsService.save(sections);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 成功添加后，删除缓存
        redisTemplate.delete(SECTION_KEY);

        long newSectionsId = sections.getId();
        return ResultUtils.success(newSectionsId);
    }


    /**
     * 删除板块表（已完成--不可随便调用）
     * 仅管理员可用
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSections(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        StpUtil.checkLogin();

        StpUtil.checkRole("ADMIN");

        long id = deleteRequest.getId();
        // 判断是否存在
        Sections oldSections = sectionsService.getById(id);
        ThrowUtils.throwIf(oldSections == null, ErrorCode.NOT_FOUND_ERROR);

        // 操作数据库
        boolean result = sectionsService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 成功添加后，删除缓存
        redisTemplate.delete(SECTION_KEY);
        return ResultUtils.success(true);
    }

    /**
     * 根据板块表列表（封装类）
     * @return
     */
    @GetMapping("/all")
    public BaseResponse<List<SectionsVO>> getAllSections() {

        // 从 Redis 获取缓存数据
        List<SectionsVO> cachedSections = (List<SectionsVO>) redisTemplate.opsForValue().get(SECTION_KEY);

        if (cachedSections != null) {
            return ResultUtils.success(cachedSections);
        }
        // 查询所有板块信息
        List<SectionsVO> sectionsVOList = sectionsService.getAllSections();

        // 更新到Redis缓存
        redisTemplate.opsForValue().set(SECTION_KEY, sectionsVOList);

        return ResultUtils.success(sectionsVOList);
    }

}
