package com.lisan.forumbackend.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.lisan.forumbackend.common.BaseResponse;
import com.lisan.forumbackend.common.DeleteRequest;
import com.lisan.forumbackend.common.ErrorCode;
import com.lisan.forumbackend.common.ResultUtils;
import com.lisan.forumbackend.exception.BusinessException;
import com.lisan.forumbackend.exception.ThrowUtils;
import com.lisan.forumbackend.model.dto.comments.CommentPagesRequest;
import com.lisan.forumbackend.model.dto.comments.CommentsAddRequest;
import com.lisan.forumbackend.model.entity.Comments;
import com.lisan.forumbackend.model.vo.CommentsVO;
import com.lisan.forumbackend.service.CommentsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 评论表接口
 * @author ぼつち
 */
@RestController
@RequestMapping("/comments")
@Slf4j
public class CommentsController {

    @Resource
    private CommentsService commentsService;
    @Resource
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    // redis记录浏览量
    private static final String VIEW_COUNT_ZSET_KEY = "topic:viewCounts:zset";  // 定义 ZSET 键名
    private static final long EXPIRATION = 7L * 24 * 60 * 60; // 7天，单位为秒

    /**
     * @author ぼつち
     * 创建评论
     * @param commentsAddRequest  评论添加请求
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> addComments(@RequestBody CommentsAddRequest commentsAddRequest, HttpServletRequest request) {
        StpUtil.checkLogin();  // 检查用户登录状态
        ThrowUtils.throwIf(commentsAddRequest == null, ErrorCode.PARAMS_ERROR);  // 参数校验

        // 将 DTO 转为实体类 Comments
        Comments comments = new Comments();
        BeanUtils.copyProperties(commentsAddRequest, comments);

        // 数据校验
        commentsService.validComments(comments);
        comments.setUserId(StpUtil.getLoginIdAsLong());  // 获取用户 ID

        // 保存评论到数据库
        boolean result = commentsService.save(comments);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 通过 RabbitMQ 发送评论消息到队列
        rabbitTemplate.convertAndSend("commentExchange", "comment", comments);

        // 返回成功提示信息，评论的保存将由 RabbitMQ 异步处理
        return ResultUtils.success(true);
    }

    /**
     * @author ぼつち
     * 删除评论
     * @param deleteRequest 删除请求
     * @param request 网络请求
     * return　Boolean
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteComments(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        StpUtil.checkLogin();

        long id = deleteRequest.getId();
        // 判断是否存在
        Comments oldComments = commentsService.getById(id);
        ThrowUtils.throwIf(oldComments == null, ErrorCode.NOT_FOUND_ERROR);

        long currentUserId = StpUtil.getLoginIdAsLong();

        long commentUserId = oldComments.getUserId();
        // 鉴权
        if (!StpUtil.hasRole("ADMIN") && commentUserId != currentUserId) {
            ThrowUtils.throwIf(true, ErrorCode.NO_AUTH_ERROR);
        }

        // 操作数据库
        boolean result = commentsService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * @author ぼつち
     * 查询指定话题评论页--懒加载用法（可视即加载）
     * @param topicId 话题id
     * @param current 当前页数
     * @param request 网络请求
     * @return List
     */
    @GetMapping("/get/commentsVo/{topicId}/{current}")
    public BaseResponse<List<CommentsVO>> getCommentsByTopicId(@PathVariable("topicId") Long topicId, @PathVariable("current") int current, HttpServletRequest request) {
        ThrowUtils.throwIf(topicId == null || topicId <= 0, ErrorCode.PARAMS_ERROR);
        CommentPagesRequest commentPagesRequest = new CommentPagesRequest();
        commentPagesRequest.setTopicId(topicId);
        commentPagesRequest.setCurrent(current);
        // 查询数据库
        List<CommentsVO> commentVOList = commentsService.getCommentsByTopicId(commentPagesRequest);

        String topicIdStr = topicId.toString();

        // 使用 ZINCRBY 命令增加有序集合中该 topicId 的分数（浏览量）
        redisTemplate.opsForZSet().incrementScore(VIEW_COUNT_ZSET_KEY, topicIdStr, 1);
        Boolean hasExpire = redisTemplate.getExpire(VIEW_COUNT_ZSET_KEY, TimeUnit.SECONDS) > 0;
        if (Boolean.FALSE.equals(hasExpire)) {
            redisTemplate.expire(VIEW_COUNT_ZSET_KEY, EXPIRATION, TimeUnit.SECONDS);
        }
        return ResultUtils.success(commentVOList);
    }

    /**
     * 查询前 30 条浏览量最高的记录
     * @author ぼつち
     * @return List
     */
    @GetMapping("/getTopViewCounts")
    public BaseResponse<List<Map<String, Object>>> getTopViewCounts() {
        // 获取有序集合中按分数从高到低的前30个记录
        Set<ZSetOperations.TypedTuple<Object>> topTopics = redisTemplate.opsForZSet().reverseRangeWithScores(VIEW_COUNT_ZSET_KEY, 0, 29);

        // 构建返回的数据列表
        List<Map<String, Object>> result = new ArrayList<>();

        // 遍历有序集合结果，将每条记录的 topicId 和 viewCount 放入 map 中
        if (topTopics != null && !topTopics.isEmpty()) {
            for (ZSetOperations.TypedTuple<Object> topic : topTopics) {
                Map<String, Object> record = new HashMap<>();
                record.put("topicId", topic.getValue().toString());  // 获取 topicId
                record.put("viewCount", topic.getScore().longValue());  // 获取浏览量（分数）
                result.add(record);
            }
        }

        return ResultUtils.success(result);
    }

}
