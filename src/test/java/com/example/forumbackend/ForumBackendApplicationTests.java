package com.example.forumbackend;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lisan.forumbackend.model.entity.Announcements;
import com.lisan.forumbackend.service.AnnouncementsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;

@SpringBootTest

class ForumBackendApplicationTests {

    @Resource
    private AnnouncementsService announcementsService;

    //自动String序列化
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Test
    void contextLoads() {
        // 创建分页对象
        Page<Announcements> page = new Page<>(1, 10);

        // 构建查询条件
        QueryWrapper<Announcements> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDelete", 0);
        queryWrapper.and(wrapper -> wrapper.like("title","喜")
                .or().like("content", "喜"));

        // 查询数据库
        Page<Announcements> announcementsPage = announcementsService.page(page, queryWrapper);
        System.out.println(announcementsPage);

    }
//
//    //json工具
//    private ObjectMapper mapper = new ObjectMapper();
//    @Test
//    void Stringteml() throws JsonProcessingException {
//        user user = new user("usertest",15,"onna");
//        String json = mapper.writeValueAsString(user);
//        stringRedisTemplate.opsForValue().set("user:2",json);
//        String vl = stringRedisTemplate.opsForValue().get("user:2");
//        //反序列化
//        user u = mapper.readValue(vl,user.class);
//        System.out.println(u);
//    }

}
