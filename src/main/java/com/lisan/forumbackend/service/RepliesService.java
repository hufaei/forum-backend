package com.lisan.forumbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lisan.forumbackend.model.entity.Replies;
import com.lisan.forumbackend.model.vo.RepliesVO;

import java.util.List;

/**
 * 回复表服务
 *
 * @author lisan
 *
 */
public interface RepliesService extends IService<Replies> {

    /**
     * 校验数据
     *
     * @param replies

     */
    void validReplies(Replies replies);

    List<RepliesVO> getRepliesByCommentId(Long commentId);


}
