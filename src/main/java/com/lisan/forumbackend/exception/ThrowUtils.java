package com.lisan.forumbackend.exception;

import com.lisan.forumbackend.common.ErrorCode;

/**
 * 抛异常工具类
 * @author ぼつち
 */
public class ThrowUtils {

    /**
     * 条件成立则抛异常
     * @param condition 判断条件
     * @param runtimeException 错误类型
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }

    /**
     * 条件成立则抛异常
     * @param condition 判断条件
     * @param errorCode 错误码
     */
    public static void throwIf(boolean condition, ErrorCode errorCode) {
        throwIf(condition, new BusinessException(errorCode));
    }

    /**
     * 条件成立则抛异常
     * @param condition 判断条件
     * @param errorCode 错误码
     * @param message 错误返回信息
     */
    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        throwIf(condition, new BusinessException(errorCode, message));
    }
}
