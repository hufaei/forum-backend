package com.lisan.forumbackend.utils;

import java.util.Random;

/**
 * 随机生成验证码工具类
 * @author ぼつち
 */
public class ValidateCodeUtils {
    /**
     * 随机生成验证码
     *
     * @param length 长度为4位或者6位
     * @return Integer
     */
    public static Integer generateValidateCode(int length) {
        Integer code;
        if (length == 4) {
            //生成随机数，最大为9999
            code = new Random().nextInt(9999);

            if (code < 1000) {
                //保证随机数为4位数字
                code = code + 1000;
            }
        } else if (length == 6) {
            //生成随机数，最大为999999
            code = new Random().nextInt(999999);
            if (code < 100000) {
                //保证随机数为6位数字
                code = code + 100000;
            }
        } else {
            throw new RuntimeException("只能生成4位或6位数字验证码");
        }
        return code;
    }

    /**
     * 随机生成指定长度字符串验证码
     *
     * @param length 长度
     * @return String
     */
    public static String generateValidateCode4String(int length) {
        Random rdm = new Random();
        String hash1 = Integer.toHexString(rdm.nextInt());
        return hash1.substring(0, length);
    }
}
