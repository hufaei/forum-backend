package com.lisan.forumbackend.model.vo;

import cn.hutool.json.JSONUtil;
import com.lisan.forumbackend.model.entity.Sections;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 板块表视图
 *
 * @author lisan
 *
 */
@Data
public class SectionsVO implements Serializable {

    /**
     * 板块id
     */
    private Long id;

    /**
     * 板块名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;


    /**
     * 封装类转对象
     *
     * @param sectionsVO
     * @return Sections
     */
    public static Sections voToObj(SectionsVO sectionsVO) {
        if (sectionsVO == null) {
            return null;
        }
        Sections sections = new Sections();
        BeanUtils.copyProperties(sectionsVO, sections);
        return sections;
    }

    /**
     * 对象转封装类
     *
     * @param sections
     * @return SectionsVO
     */
    public static SectionsVO objToVo(Sections sections) {
        if (sections == null) {
            return null;
        }
        SectionsVO sectionsVO = new SectionsVO();
        BeanUtils.copyProperties(sections, sectionsVO);
        return sectionsVO;
    }
}
