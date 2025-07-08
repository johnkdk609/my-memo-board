package com.mymemo.backend.memo.dto;

import com.mymemo.backend.entity.Memo;
import com.mymemo.backend.entity.enums.MemoCategory;
import com.mymemo.backend.entity.enums.Visibility;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemoDetailResponseDto {
    private Long id;
    private String title;
    private String content;
    private MemoCategory memoCategory;
    private Visibility visibility;
    private boolean isPinned;
    private String uuid;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MemoDetailResponseDto(Memo memo) {
        this.id = memo.getId();
        this.title = memo.getTitle();
        this.content = memo.getContent();
        this.memoCategory = memo.getMemoCategory();
        this.visibility = memo.getVisibility();
        this.isPinned = memo.isPinned();
        this.uuid = memo.getUuid();
        this.createdAt = memo.getCreatedAt();
        this.updatedAt = memo.getUpdatedAt();
    }
}
