package com.mymemo.backend.memo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mymemo.backend.entity.Memo;
import com.mymemo.backend.entity.User;
import com.mymemo.backend.entity.enums.MemoCategory;
import com.mymemo.backend.entity.enums.Visibility;
import io.swagger.v3.oas.annotations.media.Schema;

public class MemoCreateRequestDto {

    @Schema(description = "메모 제목")
    private String title;

    @Schema(description = "메모 내용")
    private String content;

    @Schema(description = "메모 카테고리")
    private MemoCategory memoCategory;

    @Schema(description = "공개 여부", defaultValue = "PUBLIC")
    private Visibility visibility;

    @Schema(description = "상단 고정 여부", defaultValue = "false", example = "false")
    @JsonProperty("pinned")
    private boolean isPinned = false;

    public MemoCreateRequestDto() {}    // @RequestBody 바인딩 시 Jackson이 사용 (필수)

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

    public Visibility getVisibility() { return visibility; }

    public boolean isPinned() { return isPinned; }

    // Setter 메서드들 (JSON -> DTO 매핑을 위해 필수)
    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setMemoCategory(MemoCategory memoCategory) {
        this.memoCategory = memoCategory;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public void setPinned(boolean pinned) {
        this.isPinned = pinned;
    }

    // 엔터티 생성 메서드. DTO -> Entity 변환 책임 (중심 로직)
    public Memo toEntity(User user) {

        return new Memo(user, title, content, memoCategory, visibility, isPinned, false, 0);
    }
}