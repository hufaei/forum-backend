package com.lisan.forumbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lisan.forumbackend.common.ErrorCode;
import com.lisan.forumbackend.exception.BusinessException;
import com.lisan.forumbackend.exception.ThrowUtils;
import com.lisan.forumbackend.mapper.SectionsMapper;
import com.lisan.forumbackend.mapper.TopicsMapper;
import com.lisan.forumbackend.mapper.UsersMapper;
import com.lisan.forumbackend.model.dto.topics.TopicPagesRequest;
import com.lisan.forumbackend.model.entity.Comments;
import com.lisan.forumbackend.model.entity.Sections;
import com.lisan.forumbackend.model.entity.Topics;
import com.lisan.forumbackend.model.entity.Users;
import com.lisan.forumbackend.model.vo.CommentsVO;
import com.lisan.forumbackend.model.vo.TopicsVO;
import com.lisan.forumbackend.service.CommentsService;
import com.lisan.forumbackend.service.TopicsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 话题表服务实现
 *
 * @author lisan
 *
 */
@Service
@Slf4j
public class TopicsServiceImpl extends ServiceImpl<TopicsMapper, Topics> implements TopicsService {

    @Autowired
    private SectionsMapper sectionsMapper;
    @Autowired
    private TopicsMapper topicsMapper;
    @Autowired
    private CommentsService commentsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UsersMapper usersMapper;
    /**
     * 校验数据
     *
     * @param topics
     */
    @Override
    public void validTopics(Topics topics) {
        ThrowUtils.throwIf(topics == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        String content= topics.getContent();
        Long sid = topics.getSectionId();

        // 补充校验规则
        ThrowUtils.throwIf(sid == null, ErrorCode.PARAMS_ERROR, "必要参数不能为空");

        if (StringUtils.isNotBlank(content)) {
            ThrowUtils.throwIf(content.length() > 150, ErrorCode.PARAMS_ERROR, "内容过长");}
        // 校验sectionId是否存在
        QueryWrapper<Sections> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", sid);
        ThrowUtils.throwIf(sectionsMapper.selectCount(queryWrapper) == 0, ErrorCode.PARAMS_ERROR, "板块不存在");

    }
    @Override
    @Transactional
    public boolean removeById(Serializable id) {
        if (id == null || (Long) id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 删除话题下的评论表记录
        List<Comments> comments = commentsService.list(new QueryWrapper<Comments>().eq("topic_id", id));
        for (Comments comment : comments) {
            // 删除评论表中的记录
            commentsService.removeById(comment.getId());
            redisTemplate.opsForValue().set("deleted_comment:"+comment.getId(),comment.getId());
        }
        // 删除话题记录
        boolean topicRemoved = super.removeById(id);
        if (!topicRemoved) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return true;
    }

    @Override
    public List<TopicsVO> getTopicsVOByUserId(Long userId) {
        // 检查用户是否存在
        Users user = usersMapper.selectById(userId);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);

        // 查询该用户下的所有话题
        List<Topics> topicsList = this.list(new QueryWrapper<Topics>().eq("user_id", userId));

        // 将Topics转换为TopicsVO并返回
        return topicsList.stream()
                .map(topics -> {
                    TopicsVO topicsVO = TopicsVO.objToVo(topics);
                    Sections section = sectionsMapper.selectById(topics.getSectionId());
                    if (section != null) {
                        topicsVO.setSectionName(section.getName());
                    }
                    return topicsVO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TopicsVO> getTopicsVOBySectionId(TopicPagesRequest topicPagesRequest) {
        Long sectionId = topicPagesRequest.getSectionId();
        // 检查sectionId是否存在
        Sections section = sectionsMapper.selectById(sectionId);
        ThrowUtils.throwIf(section == null, ErrorCode.NOT_FOUND_ERROR);

        // 设置默认加载页面大小
        int pageSize = 10;
        int currentPage = (topicPagesRequest.getCurrent() > 0) ? topicPagesRequest.getCurrent() : 1;
        // 分页查询评论，按创建时间降序排序
        Page<Topics> page = new Page<>(currentPage, pageSize);
        QueryWrapper<Topics> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("section_id", sectionId)
                .orderByDesc("created_at");  // 按创建时间降序排序
        Page<Topics> topicsPage = this.page(page, queryWrapper);

        // 将 Topic 转换为 vo 并返回
        return topicsPage.getRecords().stream()
                .map(TopicsVO::objToVo)
                .collect(Collectors.toList());
    }

    @Override
    public TopicsVO getTopicsVOById(Long topicId) {
        Topics topic = topicsMapper.selectById(topicId);
        ThrowUtils.throwIf(topic == null, ErrorCode.NOT_FOUND_ERROR);

        return TopicsVO.objToVo(topic);
    }


}
