package com.lisan.forumbackend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lisan.forumbackend.model.dto.topics.TopicPagesRequest;
import com.lisan.forumbackend.model.entity.Topics;
import com.lisan.forumbackend.model.vo.TopicsVO;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 话题表服务
 *
 * @author lisan
 *
 */
public interface TopicsService extends IService<Topics> {

    /**
     * 校验数据
     * @param topics 话题实体类
     */
    void validTopics(Topics topics);
    boolean removeById(Serializable id);

    /**
     * 根据 userId 获取话题表（封装类）
     * @param userId 用户id
     */
    List<TopicsVO> getTopicsVOByUserId(Long userId);
    /**
     * 获取话题表封装
     * @param topicPagesRequest 话题页面请求数据
     */
    List<TopicsVO> getTopicsVOBySectionId(TopicPagesRequest topicPagesRequest);

    TopicsVO getTopicsVOById(Long topicId);


    IPage<Map<String, Object>> getTopUsersByTopicCount(int current, int size);
}
