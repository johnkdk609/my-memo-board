package com.mymemo.backend.entity;

import com.mymemo.backend.entity.enums.MemoCategory;
import com.mymemo.backend.entity.enums.Visibility;
import com.mymemo.backend.global.exception.CustomException;
import jakarta.persistence.*;

import java.time.LocalDateTime;

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
    @Column(nullable = false, length = 10)
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

    public Memo(User user, String title, String content, MemoCategory memoCategory) {
        if (user == null) {
            throw new CustomException("작성자는 필수입니다.", 400);
        }
        this.user = user;

        if ((title == null || title.isBlank()) && (content == null || content.isBlank())) {
            throw new CustomException("아무 것도 작성하지 않으셨습니다.", 400);
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

        this.isPinned = false;
        this.isDeleted = false;
        this.visibility = Visibility.PUBLIC;
        this.pinOrder = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
