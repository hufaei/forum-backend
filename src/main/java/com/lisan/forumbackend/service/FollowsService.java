package com.lisan.forumbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lisan.forumbackend.model.entity.Follows;
import com.lisan.forumbackend.model.vo.FollowsVO;

import java.util.List;

/**
 * 关注表服务
 *
 * @author lisan
 *
 */
public interface FollowsService extends IService<Follows> {

    /**
     * 校验数据
     *
     * @param follows
     */
    void validFollows(Follows follows);
    /**
     * 获取关注表封装
     *
     * @param userId
     * @return
     */
    List<FollowsVO> getFollowsVO(Long userId);

    boolean isMutualFollow(Long followerid, Long followeeid);

    List<Follows> getFollowersByUserId(Long topicCreatorId);

//    /**
//     * 获取查询条件
//     *
//     * @param followsQueryRequest
//     * @return
//     */
//    QueryWrapper<Follows> getQueryWrapper(FollowsQueryRequest followsQueryRequest);
//

//    FollowsVO getFollowsVO(Follows follows, HttpServletRequest request);
//
//    /**
//     * 分页获取关注表封装
//     *
//     * @param followsPage
//     * @param request
//     * @return
//     */
//    Page<FollowsVO> getFollowsVOPage(Page<Follows> followsPage, HttpServletRequest request);

}
