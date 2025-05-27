package com.mymemo.backend.memo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PageResponseDto<T> {
    private List<T> content;        // 실제 응답 리스트
    private int page;               // 현재 페이지 번호 (0부터 시작)
    private int size;               // 페이지 당 개수
    private long totalElements;     // 전체 항목 수
    private int totalPages;         // 전체 페이지 수
    private boolean last;           // 마지막 페이지 여부
}
