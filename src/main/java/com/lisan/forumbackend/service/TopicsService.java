package com.lisan.forumbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lisan.forumbackend.model.entity.Topics;
import com.lisan.forumbackend.model.vo.TopicsVO;

import java.io.Serializable;
import java.util.List;

/**
 * 话题表服务
 *
 * @author lisan
 *
 */
public interface TopicsService extends IService<Topics> {

    /**
     * 校验数据
     *
     * @param topics
     */
    void validTopics(Topics topics);
    boolean removeById(Serializable id);

    /**
     * 根据 userId 获取话题表（封装类）
     * @param userId
     * @return
     */
    List<TopicsVO> getTopicsVOByUserId(Long userId);
    /**
     * 获取话题表封装
     *
     * @param sectionId
     * @return
     */
    List<TopicsVO> getTopicsVOBySectionId(Long sectionId);

//    /**
//     * 分页获取话题表封装
//     *
//     * @param topicsPage
//     * @param request
//     * @return
//     */
//    Page<TopicsVO> getTopicsVOPage(Page<Topics> topicsPage, HttpServletRequest request);

}
