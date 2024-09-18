package com.lisan.forumbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lisan.forumbackend.model.entity.Sections;
import com.lisan.forumbackend.model.vo.SectionsVO;

import java.io.Serializable;
import java.util.List;

/**
 * 板块表服务
 *
 * @author lisan
 *
 */
public interface SectionsService extends IService<Sections> {

    /**
     * 校验数据
     *
     * @param sections 板块实体类
     */
    void validSections(Sections sections);

    /**
     * 查询所有板块名称(视图层)
     */
    List<SectionsVO> getAllSections();

    /**
     * 通过id删除指定板块
     * @param id 板块id
     */
    boolean removeById(Serializable id);
}
