package com.lisan.forumbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lisan.forumbackend.model.dto.users.UsersLoginRequest;
import com.lisan.forumbackend.model.dto.users.UsersPagesRequest;
import com.lisan.forumbackend.model.entity.Users;
import com.lisan.forumbackend.model.vo.UsersVO;
import org.apache.catalina.User;
import org.springframework.context.annotation.Scope;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * 用户表服务
 *
 * @author lisan
 *
 */
public interface UsersService extends IService<Users> {

    /**
     * 校验数据
     *
     * @param users
     */
    Users validUsers(Users users);
    Users login(UsersLoginRequest request);

    /**
     * 获取查询条件
     *
     * @param usersPagesRequest
     * @return
     */
    QueryWrapper<Users> getQueryWrapper(UsersPagesRequest usersPagesRequest);
//
    /**
     * 获取用户表封装————用户查询账号详情
     *
     * @param users
     * @param request
     * @return
     */
    UsersVO getUsersVO(Users users, HttpServletRequest request);

    // 在 UsersServiceImpl 中重写 removeById 方法
    boolean removeById(Serializable id);

    Users updateValid(Users users);
}
