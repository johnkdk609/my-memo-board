package com.mymemo.backend.memo.dto;

import com.mymemo.backend.entity.enums.MemoCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter     // 모든 필드에 대해 getter 메서드를 자동 생성하는 Lombok 어노테이션
@AllArgsConstructor     // 모든 필드를 매개변수로 받는 생성자를 자동으로 생성한다. DTO를 사용할 때 생성자를 통해 값을 한 번에 주입할 수 있어 코드가 간결해진다.
public class MemoResponseDto {

    private Long id;
    private String title;
    private String content;
    private MemoCategory memoCategory;
    private LocalDateTime createdAt;
}
