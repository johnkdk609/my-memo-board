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

    @Schema(description = "메모 카테고리",
            allowableValues = {"WORK", "HOBBY", "PERSONAL", "URGENT", "STUDY", "IDEA", "ETC"},
            defaultValue = "ETC",
            example = "ETC")
    private MemoCategory memoCategory;

    @Schema(description = "공개 여부", defaultValue = "PRIVATE", example = "PRIVATE")
    private Visibility visibility = Visibility.PRIVATE;

    @Schema(description = "상단 고정 여부", defaultValue = "false", example = "false", hidden = true)    // Swagger 예시가 isPinned 대신 pinned 로 출력되도록 매핑 처리
    @JsonProperty("pinned")     // JSON 프로퍼티 이름을 pinned 로 설정해 직관성 확보
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

    // SRP(단일 책임 원칙) 준수 및 서비스 코드 간결화
    // 엔터티 생성 메서드. DTO -> Entity 변환 책임 (중심 로직)
    public Memo toEntity(User user) {
        Visibility resolvedVisibility = (this.visibility != null) ? this.visibility : Visibility.PRIVATE;

        return new Memo(user, title, content, memoCategory, resolvedVisibility, isPinned, false, 0);
    }
}