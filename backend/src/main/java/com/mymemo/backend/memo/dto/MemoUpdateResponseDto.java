package com.mymemo.backend.memo.dto;

import com.mymemo.backend.entity.Memo;
import com.mymemo.backend.entity.enums.MemoCategory;
import com.mymemo.backend.entity.enums.Visibility;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemoUpdateResponseDto {
    private final Long id;
    private final String title;
    private final String content;
    private final MemoCategory memoCategory;
    private final Visibility visibility;
    private final boolean isPinned;
    private final LocalDateTime updatedAt;

    public MemoUpdateResponseDto(Memo memo) {
        this.id = memo.getId();
        this.title = memo.getTitle();
        this.content = memo.getContent();
        this.memoCategory = memo.getMemoCategory();
        this.visibility = memo.getVisibility();
        this.isPinned = memo.isPinned();
        this.updatedAt = memo.getUpdatedAt();
    }
}
