package com.lisan.forumbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lisan.forumbackend.common.ErrorCode;
import com.lisan.forumbackend.exception.BusinessException;
import com.lisan.forumbackend.exception.ThrowUtils;
import com.lisan.forumbackend.mapper.CommentsMapper;
import com.lisan.forumbackend.mapper.TopicsMapper;
import com.lisan.forumbackend.mapper.UsersMapper;
import com.lisan.forumbackend.model.dto.comments.CommentPagesRequest;
import com.lisan.forumbackend.model.entity.Comments;
import com.lisan.forumbackend.model.entity.Replies;
import com.lisan.forumbackend.model.entity.Topics;
import com.lisan.forumbackend.model.entity.Users;
import com.lisan.forumbackend.model.vo.CommentsVO;
import com.lisan.forumbackend.service.CommentsService;
import com.lisan.forumbackend.service.RepliesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论表服务实现
 *
 * @author lisan
 *
 */
@Service
@Slf4j
public class CommentsServiceImpl extends ServiceImpl<CommentsMapper, Comments> implements CommentsService {

    @Resource
    private TopicsMapper topicsMapper;
    @Autowired
    private RepliesService repliesService;
    @Autowired
    private UsersMapper usersMapper;
    /**
     * 校验数据
     *
     * @param comments 评论实体类
     */
    @Override
    public void validComments(Comments comments) {
        ThrowUtils.throwIf(comments == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        String content = comments.getContent();
        Long tid = comments.getTopicId();
        // 参数校验
        ThrowUtils.throwIf(tid == null, ErrorCode.PARAMS_ERROR, "必要参数不能为空");
        if (StringUtils.isNotBlank(content)) {
            ThrowUtils.throwIf(content.length() > 100, ErrorCode.PARAMS_ERROR, "内容过长");}

        // 校验存在性
        QueryWrapper<Topics> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("id", tid);
        ThrowUtils.throwIf(topicsMapper.selectCount(queryWrapper2) == 0, ErrorCode.PARAMS_ERROR, "话题不存在");

    }


    @Override
    @Transactional
    public boolean removeById(Serializable id) {
        if (id == null || (Long) id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 删除回复表中的相关记录
        repliesService.remove(new QueryWrapper<Replies>().eq("comment_id", id));


        // 删除评论记录
        boolean commentRemoved = super.removeById(id);
        if (!commentRemoved) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }


        return true;
    }
    @Override
    public List<CommentsVO> getCommentsByTopicId(CommentPagesRequest commentPagesRequest) {
        // 检查 topicId 是否存在
        Long topicId = commentPagesRequest.getTopicId();
        QueryWrapper<Topics> existWrapper = new QueryWrapper<>();
        // 选择需要的字段
        existWrapper.select("id")
                .eq("id", topicId);
        Topics topic = topicsMapper.selectOne(existWrapper);
        ThrowUtils.throwIf(topic == null, ErrorCode.NOT_FOUND_ERROR);

        // 设置默认加载页面大小
        int pageSize = 10;
        int currentPage = (commentPagesRequest.getCurrent() > 0) ? commentPagesRequest.getCurrent() : 1;

        // 分页查询评论，按创建时间降序排序
        Page<Comments> page = new Page<>(currentPage, pageSize);
        QueryWrapper<Comments> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("topic_id", topicId)
                .orderByDesc("created_at");  // 按创建时间降序排序
        Page<Comments> commentPage = this.page(page, queryWrapper);

        // 将 Comment 转换为 CommentVO 并返回
        return commentPage.getRecords().stream()
                .map(comment -> {
                    CommentsVO commentsVO = CommentsVO.objToVo(comment);
                    // 获取用户昵称
                    Users user = usersMapper.selectById(commentsVO.getUserId());
                    String nickname = (user != null) ? user.getNickname() : "该用户不存在";
                    commentsVO.setNickName(nickname);
                    return commentsVO;
                })
                .collect(Collectors.toList());
    }


}
