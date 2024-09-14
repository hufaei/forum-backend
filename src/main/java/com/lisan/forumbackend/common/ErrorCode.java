package com.lisan.forumbackend.common;

import lombok.Getter;

/**
 * 自定义错误码
 * @author ぼつち
 */
@Getter
public enum ErrorCode {

    SUCCESS(200, "ok"),
    PARAMS_ERROR(405, "请求参数错误"),
    NOT_LOGIN_ERROR(401, "未登录"),
    NO_AUTH_ERROR(402, "无权限"),
    NOT_FOUND_ERROR(403, "请求数据不存在"),
    FORBIDDEN_ERROR(404, "禁止访问"),
    SYSTEM_ERROR(500, "系统内部异常"),
    OPERATION_ERROR(501, "操作失败");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
