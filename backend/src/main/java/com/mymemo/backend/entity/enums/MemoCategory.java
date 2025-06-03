package com.mymemo.backend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import static com.mymemo.backend.entity.enums.Visibility.PUBLIC;

public enum MemoCategory {
    WORK,
    HOBBY,
    PERSONAL,
    URGENT,
    STUDY,
    IDEA,
    ETC;     // 기본값

    @JsonCreator
    public static MemoCategory from(String value) {
        if (value == null || value.isBlank()) {
            return ETC;
        }
        try {
            return MemoCategory.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ETC;
        }
    }
}
