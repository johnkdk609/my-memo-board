package com.mymemo.backend.entity;

import com.mymemo.backend.entity.enums.MemoCategory;
import com.mymemo.backend.entity.enums.Visibility;
import com.mymemo.backend.global.exception.CustomException;
import com.mymemo.backend.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "memo")
public class Memo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length=255)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean isPinned;

    @Column(nullable = false)
    private boolean isDeleted;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Visibility visibility;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 10)
    private MemoCategory memoCategory;

    @Column(nullable = false)
    private int pinOrder;   // 상단 고정 순서, 기본 0

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected Memo() {}     // protected를 하여 불필요한 객체 생성 가능성 방지

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public void softDelete() {
        this.isDeleted = true;
    }

    public void updatePinOrder(int pinOrder) {
        this.pinOrder = pinOrder;
    }

    // 기존 생성자에 누락된 필드들 추가
    // -> visibility, isPinned, isDeleted, pinOrder 를 인자로 받아 초기화
    public Memo(User user, String title, String content, MemoCategory memoCategory, Visibility visibility, boolean isPinned, boolean isDeleted, int pinOrder) {
        if (user == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
        this.user = user;

        if ((title == null || title.isBlank()) && (content == null || content.isBlank())) {
            throw new CustomException(ErrorCode.EMPTY_MEMO);
        }

        if (title == null || title.isBlank()) {
            this.title = "untitled";
        } else {
            this.title = title;
        }
        this.content = content;

        if (memoCategory == null) {
            this.memoCategory = MemoCategory.ETC;
        } else {
            this.memoCategory = memoCategory;
        }

        if (visibility == null) {
            this.visibility = Visibility.PUBLIC;
        } else {
            this.visibility = visibility;
        }

        this.isPinned = isPinned;
        this.isDeleted = isDeleted;
        this.pinOrder = pinOrder;
    }

    // update 를 아래에서 조건부로 처리하고 있기 때문에 주석 처리
//    @PreUpdate
//    protected void onUpdate() {
//        this.updatedAt = LocalDateTime.now();
//    }

    public void update(String title, String content, MemoCategory memoCategory, Visibility visibility, boolean isPinned) {
        boolean titleChanged = (title != null && !title.equals(this.title));
        boolean contentChanged = (content != null && !content.equals(this.content));
        boolean categoryChanged = memoCategory != null && !memoCategory.equals(this.memoCategory);
        boolean visibilityChanged = visibility != null && !visibility.equals(this.visibility);

        if ((title == null || title.trim().isBlank()) && (content == null || content.isBlank())) {
            throw new CustomException(ErrorCode.EMPTY_MEMO);
        }
        if (title == null || title.trim().isEmpty()) {
            this.title = "untitled";
        } else {
            this.title = title;
        }
        this.content = content;

        this.memoCategory = (memoCategory == null) ? MemoCategory.ETC : MemoCategory.from(memoCategory.name());
        this.visibility = (visibility == null) ? Visibility.PUBLIC : Visibility.from(visibility.name());

        this.isPinned = isPinned;

        // 제목, 내용, 카테고리, 공개여부 중 하나라도 바뀌었으면 updatedAt 갱신 - pinned 여부만 바꿨으면 수정 시각은 변하지 않게 했다. UX 고려.
        if (titleChanged || contentChanged || categoryChanged || visibilityChanged) {
            this.updatedAt = LocalDateTime.now();
        }
    }
}
