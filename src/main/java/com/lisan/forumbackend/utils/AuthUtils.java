package com.lisan.forumbackend.utils;

import cn.dev33.satoken.stp.StpUtil;
import com.lisan.forumbackend.common.ErrorCode;
import com.lisan.forumbackend.exception.BusinessException;

public class AuthUtils {

    // 确保用户已登录且为普通用户
    public static void checkUser() {
        StpUtil.checkLogin(); // 确保用户已登录
        // 如果需要验证用户角色，可以在这里添加逻辑
    }

    // 确保用户已登录且为管理员
    public static void checkAdmin() {
        StpUtil.checkLogin(); // 确保用户已登录
        StpUtil.checkRole("ADMIN"); // 确保用户是管理员
    }

    // 确保用户已登录且是指定的用户
    public static void checkUserById(long userId) {
        StpUtil.checkLogin(); // 确保用户已登录
        long currentUserId = StpUtil.getLoginIdAsLong();
        if (currentUserId != userId) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR); // 当前用户不是指定用户
        }
    }

    // 获取当前登录用户的 ID
    public static long getCurrentUserId() {
        return StpUtil.getLoginIdAsLong();
    }
}
