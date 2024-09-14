package com.lisan.forumbackend.common;

/**
 * 返回工具类
 * @author ぼつち
 * 
 */
public class ResultUtils {

    /**
     * 成功
     * @param data 成功请求的数据
     * @param <T> 任意类型
     * @return T - BaseResponse
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(200, data, "ok");
    }
    public static <T> BaseResponse<T> success(T data,String message) {
        return new BaseResponse<>(200, data, message);
    }


    /**
     * 失败
     * @param errorCode 失败错误码
     * @return BaseResponse
     */
    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     * @param code 失败错误码
     * @param message 失败信息
     * @return BaseResponse
     */
    public static BaseResponse error(int code, String message) {
        return new BaseResponse(code, null, message);
    }

    /**
     * 失败
     * @param errorCode 失败错误码
     * @return BaseResponse
     */
    public static BaseResponse error(ErrorCode errorCode, String message) {
        return new BaseResponse(errorCode.getCode(), null, message);
    }
}
