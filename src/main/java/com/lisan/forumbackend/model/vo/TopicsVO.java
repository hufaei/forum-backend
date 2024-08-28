package com.lisan.forumbackend.model.vo;

import cn.hutool.json.JSONUtil;
import com.lisan.forumbackend.model.entity.Topics;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 话题表视图
 *
 * @author lisan
 *
 */
@Data
public class TopicsVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 发起者用户id
     */
    private Long userId;

    /**
     * 板块图片附件
     */
    private String image;

    /**
     * 内容
     */
    private String content;

    /**
     * 发布时间
     */
    private Date createdAt;

    /**
     * 板块名称
     */
    private String sectionName;

    /**
     * 封装类转对象
     *
     * @param topicsVO
     * @return
     */
    public static Topics voToObj(TopicsVO topicsVO) {
        if (topicsVO == null) {
            return null;
        }
        Topics topics = new Topics();
        BeanUtils.copyProperties(topicsVO, topics);
        return topics;
    }

    /**
     * 对象转封装类
     *
     * @param topics
     * @return
     */
    public static TopicsVO objToVo(Topics topics) {
        if (topics == null) {
            return null;
        }
        TopicsVO topicsVO = new TopicsVO();
        BeanUtils.copyProperties(topics, topicsVO);
        return topicsVO;
    }
}
