package com.lisan.forumbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lisan.forumbackend.common.ErrorCode;
import com.lisan.forumbackend.exception.ThrowUtils;
import com.lisan.forumbackend.mapper.FollowsMapper;
import com.lisan.forumbackend.mapper.UsersMapper;
import com.lisan.forumbackend.model.entity.Follows;
import com.lisan.forumbackend.model.entity.Users;
import com.lisan.forumbackend.model.vo.FollowsVO;
import com.lisan.forumbackend.service.FollowsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 关注表服务实现
 *
 * @author lisan
 *
 */
@Service
@Slf4j
public class FollowsServiceImpl extends ServiceImpl<FollowsMapper, Follows> implements FollowsService {

    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private FollowsMapper followsMapper;
    @Override
    public List<FollowsVO> getFollowsVO(Long userId) {
        // 检查用户是否存在
        Users user = usersMapper.selectById(userId);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);

        // 查询用户关注的用户列表
        List<Follows> followsList = this.list(new QueryWrapper<Follows>().eq("follower_id", userId));

        // 将Follows转换为FollowsVO
        return followsList.stream().map(follows -> {
            Users followee = usersMapper.selectById(follows.getFolloweeId());
            ThrowUtils.throwIf(followee == null, ErrorCode.NOT_FOUND_ERROR);

            FollowsVO followsVO = new FollowsVO();
            followsVO.setId(follows.getId());
            followsVO.setFollowerId(follows.getFollowerId());
            followsVO.setFolloweeId(follows.getFolloweeId());
            followsVO.setNickname(followee.getNickname());
            followsVO.setAvatar(followee.getAvatar());
            followsVO.setSelf_intro(followee.getSelf_intro());

            return followsVO;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean isMutualFollow(Long followerId, Long followeeId) {
        // 检查用户是否存在
        Users fer = usersMapper.selectById(followerId);
        ThrowUtils.throwIf(fer == null, ErrorCode.NOT_FOUND_ERROR);
        // 检查用户是否存在
        Users fee = usersMapper.selectById(followeeId);
        ThrowUtils.throwIf(fee == null, ErrorCode.NOT_FOUND_ERROR);
        // 检查 followerId 是否关注了 followeeId
        QueryWrapper<Follows> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("follower_id", followerId).eq("followee_id", followeeId);
        Long count1 = followsMapper.selectCount(wrapper1);

        // 检查 followeeId 是否关注了 followerId
        QueryWrapper<Follows> wrapper2 = new QueryWrapper<>();
        wrapper2.eq("follower_id", followeeId).eq("followee_id", followerId);
        Long count2 = followsMapper.selectCount(wrapper2);

        // 如果两个条件都满足，则表示互相关注
        return count1 > 0 && count2 > 0;
    }



    public List<Follows> getFollowersByUserId(Long followeeId) {
        // 使用 MyBatis-Plus 的 QueryWrapper 构建查询条件
        QueryWrapper<Follows> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("followee_id", followeeId);

        // 执行查询并返回结果
        return followsMapper.selectList(queryWrapper);
    }


    /**
     * 校验数据
     *
     * @param follows
     */
    @Override
    public void validFollows(Follows follows) {
        ThrowUtils.throwIf(follows == null, ErrorCode.PARAMS_ERROR);
        // 对象中取值
        Long followeeid = follows.getFolloweeId();
        Long followerid = follows.getFollowerId();
        // 创建数据时，参数不能为空
        ThrowUtils.throwIf(followeeid==null, ErrorCode.PARAMS_ERROR);
        // 存在性
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", followeeid);
        ThrowUtils.throwIf(usersMapper.selectCount(queryWrapper) == 0, ErrorCode.PARAMS_ERROR, "用户不存在");
        // 检查关注者和被关注者是否为同一个用户
        ThrowUtils.throwIf(followerid.equals(followeeid), ErrorCode.PARAMS_ERROR,"操作错误");
    }

//    /**
//     * 获取查询条件
//     *
//     * @param followsQueryRequest
//     * @return
//     */
//    @Override
//    public QueryWrapper<Follows> getQueryWrapper(FollowsQueryRequest followsQueryRequest) {
//        QueryWrapper<Follows> queryWrapper = new QueryWrapper<>();
//        if (followsQueryRequest == null) {
//            return queryWrapper;
//        }
//        // todo 从对象中取值
//        Long id = followsQueryRequest.getId();
//        Long notId = followsQueryRequest.getNotId();
//        String title = followsQueryRequest.getTitle();
//        String content = followsQueryRequest.getContent();
//        String searchText = followsQueryRequest.getSearchText();
//        String sortField = followsQueryRequest.getSortField();
//        String sortOrder = followsQueryRequest.getSortOrder();
//        List<String> tagList = followsQueryRequest.getTags();
//        Long userId = followsQueryRequest.getUserId();
//        // todo 补充需要的查询条件
//        // 从多字段中搜索
//        if (StringUtils.isNotBlank(searchText)) {
//            // 需要拼接查询条件
//            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
//        }
//        // 模糊查询
//        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
//        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
//        // JSON 数组查询
//        if (CollUtil.isNotEmpty(tagList)) {
//            for (String tag : tagList) {
//                queryWrapper.like("tags", "\"" + tag + "\"");
//            }
//        }
//        // 精确查询
//        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
//        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
//        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
//        // 排序规则
//        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
//                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
//                sortField);
//        return queryWrapper;
//    }
//
//    /**
//     * 获取关注表封装
//     *
//     * @param follows
//     * @param request
//     * @return
//     */
//    @Override
//    public FollowsVO getFollowsVO(Follows follows, HttpServletRequest request) {
//        // 对象转封装类
//        FollowsVO followsVO = FollowsVO.objToVo(follows);
//
//        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
//        // region 可选
//        // 1. 关联查询用户信息
//        Long userId = follows.getUserId();
//        User user = null;
//        if (userId != null && userId > 0) {
//            user = userService.getById(userId);
//        }
//        UserVO userVO = userService.getUserVO(user);
//        followsVO.setUser(userVO);
//        // 2. 已登录，获取用户点赞、收藏状态
//        long followsId = follows.getId();
//        User loginUser = userService.getLoginUserPermitNull(request);
//        if (loginUser != null) {
//            // 获取点赞
//            QueryWrapper<FollowsThumb> followsThumbQueryWrapper = new QueryWrapper<>();
//            followsThumbQueryWrapper.in("followsId", followsId);
//            followsThumbQueryWrapper.eq("userId", loginUser.getId());
//            FollowsThumb followsThumb = followsThumbMapper.selectOne(followsThumbQueryWrapper);
//            followsVO.setHasThumb(followsThumb != null);
//            // 获取收藏
//            QueryWrapper<FollowsFavour> followsFavourQueryWrapper = new QueryWrapper<>();
//            followsFavourQueryWrapper.in("followsId", followsId);
//            followsFavourQueryWrapper.eq("userId", loginUser.getId());
//            FollowsFavour followsFavour = followsFavourMapper.selectOne(followsFavourQueryWrapper);
//            followsVO.setHasFavour(followsFavour != null);
//        }
//        // endregion
//
//        return followsVO;
//    }
//
//    /**
//     * 分页获取关注表封装
//     *
//     * @param followsPage
//     * @param request
//     * @return
//     */
//    @Override
//    public Page<FollowsVO> getFollowsVOPage(Page<Follows> followsPage, HttpServletRequest request) {
//        List<Follows> followsList = followsPage.getRecords();
//        Page<FollowsVO> followsVOPage = new Page<>(followsPage.getCurrent(), followsPage.getSize(), followsPage.getTotal());
//        if (CollUtil.isEmpty(followsList)) {
//            return followsVOPage;
//        }
//        // 对象列表 => 封装对象列表
//        List<FollowsVO> followsVOList = followsList.stream().map(follows -> {
//            return FollowsVO.objToVo(follows);
//        }).collect(Collectors.toList());
//
//        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
//        // region 可选
//        // 1. 关联查询用户信息
//        Set<Long> userIdSet = followsList.stream().map(Follows::getUserId).collect(Collectors.toSet());
//        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
//                .collect(Collectors.groupingBy(User::getId));
//        // 2. 已登录，获取用户点赞、收藏状态
//        Map<Long, Boolean> followsIdHasThumbMap = new HashMap<>();
//        Map<Long, Boolean> followsIdHasFavourMap = new HashMap<>();
//        User loginUser = userService.getLoginUserPermitNull(request);
//        if (loginUser != null) {
//            Set<Long> followsIdSet = followsList.stream().map(Follows::getId).collect(Collectors.toSet());
//            loginUser = userService.getLoginUser(request);
//            // 获取点赞
//            QueryWrapper<FollowsThumb> followsThumbQueryWrapper = new QueryWrapper<>();
//            followsThumbQueryWrapper.in("followsId", followsIdSet);
//            followsThumbQueryWrapper.eq("userId", loginUser.getId());
//            List<FollowsThumb> followsFollowsThumbList = followsThumbMapper.selectList(followsThumbQueryWrapper);
//            followsFollowsThumbList.forEach(followsFollowsThumb -> followsIdHasThumbMap.put(followsFollowsThumb.getFollowsId(), true));
//            // 获取收藏
//            QueryWrapper<FollowsFavour> followsFavourQueryWrapper = new QueryWrapper<>();
//            followsFavourQueryWrapper.in("followsId", followsIdSet);
//            followsFavourQueryWrapper.eq("userId", loginUser.getId());
//            List<FollowsFavour> followsFavourList = followsFavourMapper.selectList(followsFavourQueryWrapper);
//            followsFavourList.forEach(followsFavour -> followsIdHasFavourMap.put(followsFavour.getFollowsId(), true));
//        }
//        // 填充信息
//        followsVOList.forEach(followsVO -> {
//            Long userId = followsVO.getUserId();
//            User user = null;
//            if (userIdUserListMap.containsKey(userId)) {
//                user = userIdUserListMap.get(userId).get(0);
//            }
//            followsVO.setUser(userService.getUserVO(user));
//            followsVO.setHasThumb(followsIdHasThumbMap.getOrDefault(followsVO.getId(), false));
//            followsVO.setHasFavour(followsIdHasFavourMap.getOrDefault(followsVO.getId(), false));
//        });
//        // endregion
//
//        followsVOPage.setRecords(followsVOList);
//        return followsVOPage;
//    }

}
