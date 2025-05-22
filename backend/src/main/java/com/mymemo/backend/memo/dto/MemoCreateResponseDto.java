package com.mymemo.backend.memo.dto;

import com.mymemo.backend.entity.Memo;
import com.mymemo.backend.entity.enums.MemoCategory;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter     // 모든 필드에 대해 getter 메서드를 자동 생성하는 Lombok 어노테이션
public class MemoCreateResponseDto {

    private Long id;
    private String title;
    private String content;
    private MemoCategory memoCategory;
    private LocalDateTime createdAt;

    public MemoCreateResponseDto(Memo memo) {
        this.id = memo.getId();
        this.title = memo.getTitle();
        this.content = memo.getContent();
        this.memoCategory = memo.getMemoCategory();
        this.createdAt = memo.getCreatedAt();
    }
}
