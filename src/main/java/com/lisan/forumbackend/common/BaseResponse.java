package com.lisan.forumbackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ぼつち
 * 通用返回类
 * @param <T>
 */
@Data
public class BaseResponse<T> implements Serializable {

    private Ret ret;
    private T data;

    @Data
    public static class Ret {
        private int code;
        private String message;

        public Ret(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    public BaseResponse(int code, T data, String message) {
        this.ret = new Ret(code, message);
        this.data = data;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}