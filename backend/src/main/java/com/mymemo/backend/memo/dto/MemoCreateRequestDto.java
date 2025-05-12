package com.mymemo.backend.memo.dto;

import com.mymemo.backend.entity.enums.MemoCategory;

public class MemoCreateRequestDto {

    private String title;
    private String content;
    private MemoCategory memoCategory;

    public MemoCreateRequestDto() {}    // @RequestBody 바인딩 시 Jackson이 사용 (필수)

    public MemoCreateRequestDto(String title, String content, MemoCategory memoCategory) {      // 테스트나 명시적 객체 생성 시 편의용
        this.title = title;
        this.content = content;
        this.memoCategory = memoCategory;
    }

    // Getter 메서드들
    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public MemoCategory getMemoCategory() {
        return memoCategory;
    }
}
