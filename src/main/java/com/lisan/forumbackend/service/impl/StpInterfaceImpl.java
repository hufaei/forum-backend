package com.lisan.forumbackend.service.impl;

import cn.dev33.satoken.stp.StpInterface;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lisan.forumbackend.mapper.UsersMapper;
import com.lisan.forumbackend.model.entity.Users;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class StpInterfaceImpl implements StpInterface {

    @Resource
    private UsersMapper usersMapper;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 权限检查不需要实现
        return new ArrayList<>();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> roles = new ArrayList<>();
        if (loginId != null) {
            QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("id","role").eq("id", loginId);
            Users user = usersMapper.selectOne(queryWrapper);
            if (user != null) {
                String role = user.getRole();
                if (role != null) {
                    roles.add(role);
                }
            }
        }
        return roles;
    }
}
