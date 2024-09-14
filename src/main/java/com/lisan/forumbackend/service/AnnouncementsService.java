package com.lisan.forumbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lisan.forumbackend.model.dto.announcements.AnnouncementsQueryRequest;
import com.lisan.forumbackend.model.entity.Announcements;

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


}
