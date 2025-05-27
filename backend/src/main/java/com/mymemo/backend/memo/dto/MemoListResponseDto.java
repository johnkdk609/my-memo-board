package com.mymemo.backend.memo.dto;

import com.mymemo.backend.entity.Memo;
import com.mymemo.backend.entity.enums.MemoCategory;
import com.mymemo.backend.entity.enums.Visibility;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemoListResponseDto {

    private Long id;
    private String title;
    private String content;
    private MemoCategory memoCategory;
    private Visibility visibility;
    private boolean isPinned;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String preview;     // 메모 전체 조회할 때 100자까지만 미리보기

    public MemoListResponseDto(Memo memo) {
        this.id = memo.getId();
        this.title = memo.getTitle();
        this.content = memo.getContent();
        this.memoCategory = memo.getMemoCategory();
        this.visibility = memo.getVisibility();
        this.isPinned = memo.isPinned();
        this.createdAt = memo.getCreatedAt();
        this.updatedAt = memo.getUpdatedAt();
        this.preview = generatePreview(content);
    }

    private String generatePreview(String content) {
        if (content == null || content.isBlank()) {
            return "";
        }
        String preview;
        if (content.length() <= 100) {
            preview = content;
        } else {
            preview = content.substring(0, 100) + "...";
        }
        return preview;
    }

    public static MemoListResponseDto from(Memo memo) {
        return new MemoListResponseDto(memo);
    }
}