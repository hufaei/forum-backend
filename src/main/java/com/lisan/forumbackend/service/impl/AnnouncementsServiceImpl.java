package com.lisan.forumbackend.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lisan.forumbackend.common.ErrorCode;
import com.lisan.forumbackend.exception.ThrowUtils;
import com.lisan.forumbackend.mapper.AnnouncementsMapper;
import com.lisan.forumbackend.model.dto.announcements.AnnouncementsQueryRequest;
import com.lisan.forumbackend.model.entity.Announcements;
import com.lisan.forumbackend.model.vo.AnnouncementsVO;
import com.lisan.forumbackend.service.AnnouncementsService;
import com.lisan.forumbackend.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * 通告表服务实现
 *
 * @author lisan
 *
 */
@Service
@Slf4j
public class AnnouncementsServiceImpl extends ServiceImpl<AnnouncementsMapper, Announcements> implements AnnouncementsService {

    @Resource
    private AnnouncementsMapper announcementsMapper;

    /**
     * 添加时校验数据--add
     * @param announcements
     */
    @Override
    public void validAnnouncements(Announcements announcements) {
        ThrowUtils.throwIf(announcements == null, ErrorCode.PARAMS_ERROR);
        // 取值
        String title = announcements.getTitle();
        String content = announcements.getContent();
        // 判空 && 判规格
        if (StringUtils.isNotBlank(title) && StringUtils.isNotBlank(content)) {
            ThrowUtils.throwIf(title.length() > 10, ErrorCode.PARAMS_ERROR, "标题过长");
            ThrowUtils.throwIf(content.length() > 200, ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }
    /**
     * 获取通告表封装
     *
     * @param announcements
     * @param request
     * @return
     */
    @Override
    public AnnouncementsVO getAnnouncementsVO(Announcements announcements, HttpServletRequest request) {
        // 对象转封装类
        AnnouncementsVO announcementsVO = AnnouncementsVO.objToVo(announcements);

        Long id = announcementsVO.getId();
        String title = announcementsVO.getTitle();
        String content = announcementsVO.getContent();
        // 判空
        if (id == null) {
            ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR, "不存在的信息");
        }
        ThrowUtils.throwIf(StringUtils.isEmpty(title), ErrorCode.PARAMS_ERROR, "未读取到信息");
        ThrowUtils.throwIf(StringUtils.isEmpty(content), ErrorCode.PARAMS_ERROR, "未读取到信息");

        return announcementsVO;
    }

    /**
     * 获取查询条件
     *
     * @param announcementsQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Announcements> getQueryWrapper(AnnouncementsQueryRequest announcementsQueryRequest) {
        QueryWrapper<Announcements> queryWrapper = new QueryWrapper<>();
        if (announcementsQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        String searchText = announcementsQueryRequest.getSearchText();
        String sortField = announcementsQueryRequest.getSortField();
        String sortOrder = announcementsQueryRequest.getSortOrder();
        Long id = announcementsQueryRequest.getId();
        // 若有——id精确查询
        if(id!=null){
            queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
            return queryWrapper;
        }

        // 搜索词——标题、内容搜索搜索
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
            }

//      排序规则
//      排序字段按照实体类或者sql表的名称均可
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals("ascend"),
                sortField);

        return queryWrapper;
    }

}
