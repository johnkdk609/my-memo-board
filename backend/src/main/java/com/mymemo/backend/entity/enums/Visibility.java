package com.mymemo.backend.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Visibility {
    PUBLIC, PRIVATE;

    @JsonCreator
    public static Visibility from(String value) {
        if (value == null || value.isBlank()) {
            return PUBLIC;
        }
        try {
            return Visibility.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PUBLIC;
        }
    }
}
