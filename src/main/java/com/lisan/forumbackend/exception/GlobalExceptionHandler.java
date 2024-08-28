package com.lisan.forumbackend.exception;

import cn.dev33.satoken.exception.NotLoginException;
import com.lisan.forumbackend.common.BaseResponse;
import com.lisan.forumbackend.common.ErrorCode;
import com.lisan.forumbackend.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.lisan.forumbackend.common.ErrorCode.NOT_LOGIN_ERROR;

/**
 * 全局异常处理器
 *
 * treay
 * 
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }

    @ExceptionHandler(NotLoginException.class)
    @ResponseBody
    public BaseResponse<?> handleNotLoginException(NotLoginException ex) {

        return ResultUtils.error(NOT_LOGIN_ERROR);
    }
}
