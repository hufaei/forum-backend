package com.lisan.forumbackend.model.dto.announcements;

import com.lisan.forumbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
/**
 * 查询通告表请求
 *
 * @author lisan
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AnnouncementsQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 搜索词
     */
    private String searchText;



    private static final long serialVersionUID = 1L;
}