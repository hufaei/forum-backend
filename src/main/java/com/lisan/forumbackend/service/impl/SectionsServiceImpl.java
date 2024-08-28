package com.lisan.forumbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lisan.forumbackend.common.ErrorCode;
import com.lisan.forumbackend.exception.BusinessException;
import com.lisan.forumbackend.exception.ThrowUtils;
import com.lisan.forumbackend.mapper.SectionsMapper;
import com.lisan.forumbackend.model.entity.Sections;
import com.lisan.forumbackend.model.entity.Topics;
import com.lisan.forumbackend.model.entity.Users;
import com.lisan.forumbackend.model.vo.SectionsVO;
import com.lisan.forumbackend.service.SectionsService;
import com.lisan.forumbackend.service.TopicsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 板块表服务实现
 *
 * @author lisan
 *
 */
@Service
@Slf4j
public class SectionsServiceImpl extends ServiceImpl<SectionsMapper, Sections> implements SectionsService {

    @Resource
    private SectionsMapper sectionsMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 校验数据
     *
     * @param sections
     */
    @Override
    public void validSections(Sections sections) {
        ThrowUtils.throwIf(sections == null, ErrorCode.PARAMS_ERROR);
        // 取值
        String name = sections.getName();
        String description = sections.getDescription();
        // 判空 && 判规格
        if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(description)) {
            ThrowUtils.throwIf(name.length() > 10, ErrorCode.PARAMS_ERROR, "名称过长");
            ThrowUtils.throwIf(description.length()>30,ErrorCode.PARAMS_ERROR, "描述过长");
        }
        QueryWrapper<Sections> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",name);
        Sections existingSection = sectionsMapper.selectOne(queryWrapper);
        ThrowUtils.throwIf(existingSection != null, ErrorCode.PARAMS_ERROR, "板块重复");

    }
    @Autowired
    private TopicsService topicsService;

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
        if (id == null || (Long) id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 删除话题表中的相关记录
        List<Topics> topics = topicsService.list(new QueryWrapper<Topics>().eq("section_id", id));
        for (Topics topic : topics) {
            topicsService.removeById(topic.getId());
            redisTemplate.opsForValue().set("deleted_topic:"+topic.getId(),topic.getId());
        }
        // 删除板块记录
        boolean sectionRemoved = super.removeById(id);
        if (!sectionRemoved) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }



        return true;
    }

    @Override
    public List<SectionsVO> getAllSections() {
        // 查询所有板块信息
        List<Sections> sectionsList = this.list();

        // 将Sections转换为SectionsVO并返回
        return sectionsList.stream()
                .map(SectionsVO::objToVo)
                .collect(Collectors.toList());
    }
}
