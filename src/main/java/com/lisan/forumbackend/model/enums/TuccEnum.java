package com.lisan.forumbackend.model.enums;

import lombok.Getter;

/**
 * 图床数据枚举
 * Enum for handling Tucc data such as avatar ID, board IDs, upload URL, and token.
 * @author ぼつち
 */
@Getter
public enum TuccEnum {

    AVATAR_ID(2767),
    SECTION_1(2764),
    SECTION_2(2765),
    SECTION_3(2766),
    UPLOAD_URL("http://api.tucang.cc/api/v1/upload"),
    TOKEN("1725346767898e4e45d514ef64a21b0da2240f5d2195e");

    private final Object value;

    TuccEnum(Object value) {
        this.value = value;
    }

}
