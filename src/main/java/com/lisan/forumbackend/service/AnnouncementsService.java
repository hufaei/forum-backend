package com.lisan.forumbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lisan.forumbackend.model.dto.announcements.AnnouncementsQueryRequest;
import com.lisan.forumbackend.model.entity.Announcements;
import com.lisan.forumbackend.model.vo.AnnouncementsVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 通告表服务
 *
 * @author lisan
 *
 */
public interface AnnouncementsService extends IService<Announcements> {


    /**
     * 校验数据
     *
     * @param announcements
     */
    void validAnnouncements(Announcements announcements);

    /**
     * 获取查询条件
     *
     * @param announcementsQueryRequest
     * @return
     */
    QueryWrapper<Announcements> getQueryWrapper(AnnouncementsQueryRequest announcementsQueryRequest);

    /**
     * 获取通告表封装
     *
     * @param announcements
     * @param request
     * @return
     */
    AnnouncementsVO getAnnouncementsVO(Announcements announcements, HttpServletRequest request);

}
