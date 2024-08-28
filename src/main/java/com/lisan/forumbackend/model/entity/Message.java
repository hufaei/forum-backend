package com.lisan.forumbackend.model.entity;

import lombok.Data;

@Data
public class Message {

    private String from;
    private String to;
    private String msg;
    private Long timestamp;

}