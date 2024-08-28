package com.lisan.forumbackend.model.vo;

import cn.hutool.json.JSONUtil;
import com.lisan.forumbackend.model.entity.Follows;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 关注表视图
 *
 * @author lisan
 *
 */
@Data
public class FollowsVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 关注者id
     */
    private Long followerId;

    /**
     * 被关注者id
     */
    private Long followeeId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 自我介绍
     */
    private String self_intro;

    /**
     * 封装类转对象
     *
     * @param followsVO
     * @return Follows
     */
    public static Follows voToObj(FollowsVO followsVO) {
        if (followsVO == null) {
            return null;
        }
        Follows follows = new Follows();
        BeanUtils.copyProperties(followsVO, follows);
        return follows;
    }

    /**
     * 对象转封装类
     *
     * @param follows
     * @return FollowsVO
     */
    public static FollowsVO objToVo(Follows follows) {
        if (follows == null) {
            return null;
        }
        FollowsVO followsVO = new FollowsVO();
        BeanUtils.copyProperties(follows, followsVO);
        return followsVO;
    }
}
