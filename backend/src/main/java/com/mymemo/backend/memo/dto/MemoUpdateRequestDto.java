package com.mymemo.backend.memo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mymemo.backend.entity.enums.MemoCategory;
import com.mymemo.backend.entity.enums.Visibility;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class MemoUpdateRequestDto {

    private String title;

    private String content;

    private MemoCategory memoCategory;

    private Visibility visibility;

    @Schema(defaultValue = "false", example = "false")
    @JsonProperty("pinned")
    private boolean isPinned;
}
