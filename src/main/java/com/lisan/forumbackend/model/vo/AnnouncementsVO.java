package com.lisan.forumbackend.model.vo;

import cn.hutool.json.JSONUtil;
import com.lisan.forumbackend.model.entity.Announcements;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 通告表视图
 *
 * @author lisan
 *
 */
@Data
public class AnnouncementsVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 更新时间
     */
    private Date updatedAt;


    /**
     * 封装类转对象
     *
     * @param announcementsVO
     * @return Announcements
     */
    public static Announcements voToObj(AnnouncementsVO announcementsVO) {
        if (announcementsVO == null) {
            return null;
        }
        Announcements announcements = new Announcements();
        BeanUtils.copyProperties(announcementsVO, announcements);
        return announcements;
    }

    /**
     * 对象转封装类
     *
     * @param announcements
     * @return AnnouncementsVO
     */
    public static AnnouncementsVO objToVo(Announcements announcements) {
        if (announcements == null) {
            return null;
        }
        AnnouncementsVO announcementsVO = new AnnouncementsVO();
        BeanUtils.copyProperties(announcements, announcementsVO);
        return announcementsVO;
    }
}
