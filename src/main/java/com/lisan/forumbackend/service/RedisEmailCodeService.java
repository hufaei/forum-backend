package com.lisan.forumbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 对邮箱验证码的一系列操作
 */
@Service
public class RedisEmailCodeService {

    @Autowired
    private RedisTemplate redisTemplate;

    // 存储验证码并设置有效期（5分钟）
    public void saveAuthCode(String email, String authCode) {
        redisTemplate.opsForValue().set(email, authCode, 5, TimeUnit.MINUTES);
    }

    // 根据邮箱获取验证码
    public String getAuthCode(String email) {
        return (String) redisTemplate.opsForValue().get(email);
    }

    // 删除验证码（验证成功后）
    public void deleteAuthCode(String email) {
        redisTemplate.delete(email);
    }
}
