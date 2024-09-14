package com.lisan.forumbackend.controller;

import com.lisan.forumbackend.model.entity.*;
import com.lisan.forumbackend.service.*;
import io.goeasy.GoEasy;
import io.goeasy.publish.GoEasyError;
import io.goeasy.publish.PublishListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ぼつち
 * Comment队列监听器
 */
@Service
@Slf4j
public class MqListener {

    @Resource
    private CommentsService commentsService;
    @Resource
    private TopicsService topicsService;
    @Resource
    private UsersService usersService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private FollowsService followsService;
    @Resource
    private RepliesService repliesService;
    //GoEasy Java SDK需升级至0.4.2以上
    private final GoEasy goEasy = new GoEasy("http://rest-hz.goeasy.io","BC-3daf9f10c85a43279b82415993224f65");


    // 监听 RabbitMQ 的评论队列

    @RabbitListener(queues = "topicQueue")
    public void handleNewTopic(Topics topics) {
        log.info("收到新的话题: {}", topics.getContent());

        Long topicCreatorId = topics.getUserId();  // 获取话题创建者ID
        Users users = usersService.getById(topicCreatorId);

        // 查找关注该话题创建者的所有粉丝
        List<Follows> followers = followsService.getFollowersByUserId(topicCreatorId);

        // 向每个粉丝发送通知
        for (Follows follow : followers) {
            Long followerId = follow.getFollowerId();  // 获取粉丝ID

            // 向粉丝的通知 channel 发送通知
            goEasy.publish("channel_notification_" + followerId, "您关注的用户-" + users.getNickname() + " 发布了新话题: " + topics.getContent(),1, new PublishListener() {
                @Override
                public void onSuccess() {
                    log.info("关注通知发送成功: followerId={}", followerId);
                }

                @Override
                public void onFailed(GoEasyError error) {
                    log.error("关注通知发送失败: {}, {}", error.getCode(), error.getContent());
                }
            });
        }

        // 保存话题到数据库
        boolean result = topicsService.save(topics);
        if (result) {
            log.info("话题保存成功: {}", topics.getId());
        } else {
            log.error("话题保存失败: {}", topics.getId());
        }
    }


    @RabbitListener(queues = "commentQueue")
    public void handleNewComment(Comments comments) {
        log.info("收到新的评论: {}", comments.getContent());

        // 查找话题的创建者
        Topics topic = topicsService.getById(comments.getTopicId());
        Long topicOwnerId = topic.getUserId();  // 获取话题创建者ID

        // 向统一的通知 channel 发送通知
        goEasy.publish("channel_notification_" + topicOwnerId, "有人评论了您的话题: " + comments.getContent(),1, new PublishListener() {
            @Override
            public void onSuccess() {
                log.info("评论通知发送成功: topicOwnerId={}", topicOwnerId);
            }

            @Override
            public void onFailed(GoEasyError error) {
                log.error("评论通知发送失败: {}, {}", error.getCode(), error.getContent());
            }
        });
    }

    @RabbitListener(queues = "replyQueue")
    public void handleNewReply(Replies replies) {
        log.info("收到新的回复: {}", replies.getContent());

        // 查找评论的创建者
        Comments comment = commentsService.getById(replies.getCommentId());
        Long commentOwnerId = comment.getUserId();  // 获取评论创建者ID

        // 向统一的通知 channel 发送通知
        goEasy.publish("channel_notification_" + commentOwnerId, "有人回复了您的评论: " + replies.getContent(),1, new PublishListener() {
            @Override
            public void onSuccess() {
                log.info("回复通知发送成功: commentOwnerId={}", commentOwnerId);
            }

            @Override
            public void onFailed(GoEasyError error) {
                log.error("回复通知发送失败: {}, {}", error.getCode(), error.getContent());
            }
        });
    }
    @RabbitListener(queues = "thumbQueue")
    public void handleThumb(Long topicId) {
        log.info("收到点赞的消息，话题ID: {}", topicId);

        Topics topic = topicsService.getById(topicId);
        if (topic == null) {
            log.error("点赞失败，未找到话题ID: {}", topicId);
            return;
        }

        //使用 SETNX
        String redisKey = "topic:thumbs:" + topicId;

        redisTemplate.opsForValue().setIfAbsent(redisKey, 0);

        redisTemplate.opsForValue().increment(redisKey);
        log.info("点赞+1", topicId);

        // 向话题持有者发送通知
        Long topicOwnerId = topic.getUserId();  // 获取话题的创建者ID
        String notificationMessage = "您的话题"+topic.getContent()+"收到了一个新的赞！";

        goEasy.publish("channel_thumb_" + topicOwnerId, notificationMessage,1, new PublishListener() {
            @Override
            public void onSuccess() {
                log.info("点赞通知发送成功，发送给用户ID: {}", topicOwnerId);
            }
            @Override
            public void onFailed(GoEasyError error) {
                log.error("点赞通知发送失败，错误代码: {}, 错误信息: {}", error.getCode(), error.getContent());
            }
        });
    }
}
