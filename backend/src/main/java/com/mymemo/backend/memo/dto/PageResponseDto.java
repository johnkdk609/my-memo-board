package com.mymemo.backend.memo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 공통 페이징 응답 DTO
 * - 모든 페이징 API 응답에서 일관된 형태로 사용할 수 있도록 제네릭 타입으로 설계
 * @param <T> 페이지 내용의 DTO 타입
 */
@Getter
@AllArgsConstructor
public class PageResponseDto<T> {
    private List<T> content;        // 실제 응답 리스트
    private int page;               // 현재 페이지 번호 (0부터 시작)
    private int size;               // 페이지당 항목 수
    private long totalElements;     // 전체 항목 수
    private int totalPages;         // 전체 페이지 수
    private boolean last;           // 마지막 페이지 여부
}
