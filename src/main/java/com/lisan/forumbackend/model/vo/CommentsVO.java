package com.lisan.forumbackend.model.vo;

import cn.hutool.json.JSONUtil;
import com.lisan.forumbackend.model.entity.Comments;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 评论表视图
 *
 * @author lisan
 *
 */
@Data
public class CommentsVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String nickName;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 发布时间
     */
    private Date createdAt;

    /**
     * 封装类转对象
     *
     * @param commentsVO
     * @return Comments
     */
    public static Comments voToObj(CommentsVO commentsVO) {
        if (commentsVO == null) {
            return null;
        }
        Comments comments = new Comments();
        BeanUtils.copyProperties(commentsVO, comments);
        return comments;
    }

    /**
     * 对象转封装类
     *
     * @param comments
     * @return CommentsVO
     */
    public static CommentsVO objToVo(Comments comments) {
        if (comments == null) {
            return null;
        }
        CommentsVO commentsVO = new CommentsVO();
        BeanUtils.copyProperties(comments, commentsVO);
        return commentsVO;
    }
}
