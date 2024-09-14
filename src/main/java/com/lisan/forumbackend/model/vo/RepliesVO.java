package com.lisan.forumbackend.model.vo;

import com.lisan.forumbackend.model.entity.Replies;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 回复表视图
 *
 * @author lisan
 *
 */
@Data
public class RepliesVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 回复用户 id
     */
    private Long userId;

    /**
     * 回复用户名称
     */
    private String nickName;

    /**
     * 内容
     */
    private String content;

    /**
     * 发布时间
     */
    private Date createdAt;


    /**
     * 封装类转对象
     *
     * @param repliesVO 视图类
     * @return Replies
     */
    public static Replies voToObj(RepliesVO repliesVO) {
        if (repliesVO == null) {
            return null;
        }
        Replies replies = new Replies();
        BeanUtils.copyProperties(repliesVO, replies);
        return replies;
    }

    /**
     * 对象转封装类
     *
     * @param replies 实体类
     * @return RepliesVO
     */
    public static RepliesVO objToVo(Replies replies) {
        if (replies == null) {
            return null;
        }
        RepliesVO repliesVO = new RepliesVO();
        BeanUtils.copyProperties(replies, repliesVO);
        return repliesVO;
    }
}
