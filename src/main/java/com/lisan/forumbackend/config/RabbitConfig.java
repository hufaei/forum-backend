package com.lisan.forumbackend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
public class RabbitConfig implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnsCallback {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct // 对象创建之后立即执行的方法——mq增强
    public void initRabbitTemplate(){
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        // 发送到队列成功调用

    }

    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        // 发送失败调用

    }

}
