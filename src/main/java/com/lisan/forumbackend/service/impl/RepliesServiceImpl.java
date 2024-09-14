package com.lisan.forumbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lisan.forumbackend.common.ErrorCode;
import com.lisan.forumbackend.exception.ThrowUtils;
import com.lisan.forumbackend.mapper.CommentsMapper;
import com.lisan.forumbackend.mapper.RepliesMapper;
import com.lisan.forumbackend.mapper.UsersMapper;
import com.lisan.forumbackend.model.entity.Comments;
import com.lisan.forumbackend.model.entity.Replies;
import com.lisan.forumbackend.model.entity.Users;
import com.lisan.forumbackend.model.vo.RepliesVO;
import com.lisan.forumbackend.service.RepliesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 回复表服务实现
 * 基本CDRU操作
 * @author ぼつち
 *
 */
@Service
@Slf4j
public class RepliesServiceImpl extends ServiceImpl<RepliesMapper, Replies> implements RepliesService {

    @Resource
    private CommentsMapper commentsMapper;
    @Autowired
    private UsersMapper usersMapper;
    /**
     * 校验数据
     * @author ぼつち
     * @param replies 验证回复数据
     */
    @Override
    public void validReplies(Replies replies) {
        ThrowUtils.throwIf(replies == null, ErrorCode.PARAMS_ERROR);
        // 取值
        String content = replies.getContent();
        Long cid = replies.getCommentId();

        if (StringUtils.isNotBlank(content)) {
            ThrowUtils.throwIf(content.length() > 100, ErrorCode.PARAMS_ERROR, "内容过长");
        }
        // 校验存在性
        QueryWrapper<Comments> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", cid);
        ThrowUtils.throwIf(commentsMapper.selectCount(queryWrapper) == 0, ErrorCode.PARAMS_ERROR, "当前评论不存在");

    }

    @Override
    public List<RepliesVO> getRepliesByCommentId(Long commentId) {
        // 检查commentId是否存在
        Comments comment = commentsMapper.selectById(commentId);
        ThrowUtils.throwIf(comment == null, ErrorCode.NOT_FOUND_ERROR);

        // 查询该评论下的所有回复
        List<Replies> repliesList = this.list(new QueryWrapper<Replies>().eq("comment_id", commentId));

        // 将Replies转换为RepliesVO并返回
        return repliesList.stream()
                .map(replies -> {
                    RepliesVO repliesVO = RepliesVO.objToVo(replies);
                    // 获取用户昵称
                    Users user = usersMapper.selectById(repliesVO.getUserId());
                    String nickname = (user != null) ? user.getNickname() : "该用户不存在";
                    repliesVO.setNickName(nickname);
                    return repliesVO;
                })
                .collect(Collectors.toList());
    }

//    /**
//     * 获取查询条件
//     *
//     * @param repliesQueryRequest
//     * @return
//     */
//    @Override
//    public QueryWrapper<Replies> getQueryWrapper(RepliesQueryRequest repliesQueryRequest) {
//        QueryWrapper<Replies> queryWrapper = new QueryWrapper<>();
//        if (repliesQueryRequest == null) {
//            return queryWrapper;
//        }
//        // todo 从对象中取值
//        Long id = repliesQueryRequest.getId();
//        Long notId = repliesQueryRequest.getNotId();
//        String title = repliesQueryRequest.getTitle();
//        String content = repliesQueryRequest.getContent();
//        String searchText = repliesQueryRequest.getSearchText();
//        String sortField = repliesQueryRequest.getSortField();
//        String sortOrder = repliesQueryRequest.getSortOrder();
//        List<String> tagList = repliesQueryRequest.getTags();
//        Long userId = repliesQueryRequest.getUserId();
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
//     * 获取回复表封装
//     *
//     * @param replies
//     * @param request
//     * @return
//     */
//    @Override
//    public RepliesVO getRepliesVO(Replies replies, HttpServletRequest request) {
//        // 对象转封装类
//        RepliesVO repliesVO = RepliesVO.objToVo(replies);
//
//        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
//        // region 可选
//        // 1. 关联查询用户信息
//        Long userId = replies.getUserId();
//        User user = null;
//        if (userId != null && userId > 0) {
//            user = userService.getById(userId);
//        }
//        UserVO userVO = userService.getUserVO(user);
//        repliesVO.setUser(userVO);
//        // 2. 已登录，获取用户点赞、收藏状态
//        long repliesId = replies.getId();
//        User loginUser = userService.getLoginUserPermitNull(request);
//        if (loginUser != null) {
//            // 获取点赞
//            QueryWrapper<RepliesThumb> repliesThumbQueryWrapper = new QueryWrapper<>();
//            repliesThumbQueryWrapper.in("repliesId", repliesId);
//            repliesThumbQueryWrapper.eq("userId", loginUser.getId());
//            RepliesThumb repliesThumb = repliesThumbMapper.selectOne(repliesThumbQueryWrapper);
//            repliesVO.setHasThumb(repliesThumb != null);
//            // 获取收藏
//            QueryWrapper<RepliesFavour> repliesFavourQueryWrapper = new QueryWrapper<>();
//            repliesFavourQueryWrapper.in("repliesId", repliesId);
//            repliesFavourQueryWrapper.eq("userId", loginUser.getId());
//            RepliesFavour repliesFavour = repliesFavourMapper.selectOne(repliesFavourQueryWrapper);
//            repliesVO.setHasFavour(repliesFavour != null);
//        }
//        // endregion
//
//        return repliesVO;
//    }
//
//    /**
//     * 分页获取回复表封装
//     *
//     * @param repliesPage
//     * @param request
//     * @return
//     */
//    @Override
//    public Page<RepliesVO> getRepliesVOPage(Page<Replies> repliesPage, HttpServletRequest request) {
//        List<Replies> repliesList = repliesPage.getRecords();
//        Page<RepliesVO> repliesVOPage = new Page<>(repliesPage.getCurrent(), repliesPage.getSize(), repliesPage.getTotal());
//        if (CollUtil.isEmpty(repliesList)) {
//            return repliesVOPage;
//        }
//        // 对象列表 => 封装对象列表
//        List<RepliesVO> repliesVOList = repliesList.stream().map(replies -> {
//            return RepliesVO.objToVo(replies);
//        }).collect(Collectors.toList());
//
//        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
//        // region 可选
//        // 1. 关联查询用户信息
//        Set<Long> userIdSet = repliesList.stream().map(Replies::getUserId).collect(Collectors.toSet());
//        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
//                .collect(Collectors.groupingBy(User::getId));
//        // 2. 已登录，获取用户点赞、收藏状态
//        Map<Long, Boolean> repliesIdHasThumbMap = new HashMap<>();
//        Map<Long, Boolean> repliesIdHasFavourMap = new HashMap<>();
//        User loginUser = userService.getLoginUserPermitNull(request);
//        if (loginUser != null) {
//            Set<Long> repliesIdSet = repliesList.stream().map(Replies::getId).collect(Collectors.toSet());
//            loginUser = userService.getLoginUser(request);
//            // 获取点赞
//            QueryWrapper<RepliesThumb> repliesThumbQueryWrapper = new QueryWrapper<>();
//            repliesThumbQueryWrapper.in("repliesId", repliesIdSet);
//            repliesThumbQueryWrapper.eq("userId", loginUser.getId());
//            List<RepliesThumb> repliesRepliesThumbList = repliesThumbMapper.selectList(repliesThumbQueryWrapper);
//            repliesRepliesThumbList.forEach(repliesRepliesThumb -> repliesIdHasThumbMap.put(repliesRepliesThumb.getRepliesId(), true));
//            // 获取收藏
//            QueryWrapper<RepliesFavour> repliesFavourQueryWrapper = new QueryWrapper<>();
//            repliesFavourQueryWrapper.in("repliesId", repliesIdSet);
//            repliesFavourQueryWrapper.eq("userId", loginUser.getId());
//            List<RepliesFavour> repliesFavourList = repliesFavourMapper.selectList(repliesFavourQueryWrapper);
//            repliesFavourList.forEach(repliesFavour -> repliesIdHasFavourMap.put(repliesFavour.getRepliesId(), true));
//        }
//        // 填充信息
//        repliesVOList.forEach(repliesVO -> {
//            Long userId = repliesVO.getUserId();
//            User user = null;
//            if (userIdUserListMap.containsKey(userId)) {
//                user = userIdUserListMap.get(userId).get(0);
//            }
//            repliesVO.setUser(userService.getUserVO(user));
//            repliesVO.setHasThumb(repliesIdHasThumbMap.getOrDefault(repliesVO.getId(), false));
//            repliesVO.setHasFavour(repliesIdHasFavourMap.getOrDefault(repliesVO.getId(), false));
//        });
//        // endregion
//
//        repliesVOPage.setRecords(repliesVOList);
//        return repliesVOPage;
//    }

}
