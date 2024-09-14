package com.lisan.forumbackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求
 * @author ぼつち
 * 
 */
@Data
public class DeleteRequest implements Serializable {

    private Long id;

    private static final long serialVersionUID = 1L;
}