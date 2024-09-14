package com.lisan.forumbackend.model.dto.users;

import com.lisan.forumbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询关注信息请求
 *
 * @author lisan
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UsersPagesRequest extends PageRequest implements Serializable {

    /**
     * 用户id
     */
    private Long id;



    private static final long serialVersionUID = 1L;
}