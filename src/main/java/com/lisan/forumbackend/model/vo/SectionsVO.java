package com.lisan.forumbackend.model.vo;

import com.lisan.forumbackend.model.entity.Sections;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

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
     * @param sectionsVO 视图类
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
     * @param sections 实体类
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
