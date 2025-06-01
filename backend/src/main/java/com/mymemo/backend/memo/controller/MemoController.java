package com.mymemo.backend.memo.controller;

import com.mymemo.backend.global.util.SecurityUtil;
import com.mymemo.backend.memo.dto.MemoCreateRequestDto;
import com.mymemo.backend.memo.dto.MemoCreateResponseDto;
import com.mymemo.backend.memo.dto.MemoListResponseDto;
import com.mymemo.backend.memo.dto.PageResponseDto;
import com.mymemo.backend.memo.service.MemoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/memos")
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    /**
     * 메모 생성 API
     * @param requestDto 제목, 카테고리, 내용
     * @return 메모 생성 성공 메시지
     */
    @PostMapping
    public ResponseEntity<MemoCreateResponseDto> createMemo(@RequestBody MemoCreateRequestDto requestDto) {
        MemoCreateResponseDto responseDto = memoService.createMemo(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * [GET] /api/memos
     * 페이징 처리된 메모 목록을 조회하는 API
     * @PageableDefault: 기본 페이지 크기와 정렬 기준 지정 (10개씩, 최신순)
     * @ParameterObject: Swagger UI에 pageable 파라미터 자동 반영
     */
    @GetMapping
    public ResponseEntity<PageResponseDto<MemoListResponseDto>> getMemos(
            @ParameterObject @PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
//        long start = System.currentTimeMillis();

        // 현재 로그인된 사용자의 이메일을 가져옴
        String email = SecurityUtil.getCurrentUserEmail();

        // MemoService를 통해 페이징 처리된 메모 목록 응답을 받음
        PageResponseDto<MemoListResponseDto> response = memoService.getMemos(email, pageable);

//        long end = System.currentTimeMillis();
//        log.info("[getAllMemos] 메모 조회 소요 시간: {} ms", (end - start));

        return ResponseEntity.ok(response);
    }

    /**
     * [GET] /api/memos/search
     * 제목에 키워드가 포함된 메모들을 페이징 처리하여 조회
     * @param keyword 검색 키워드 (제목 기준, 대소문자 무시)
     * @param pageable 페이징 및 정렬 정보
     * @return 페이징된 메모 응답
     */
    @GetMapping("/search")
    public ResponseEntity<PageResponseDto<MemoListResponseDto>> searchMemosByTitle(
            @RequestParam String keyword,
            @ParameterObject @PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {

//        long start = System.currentTimeMillis();

        String email = SecurityUtil.getCurrentUserEmail();

        PageResponseDto<MemoListResponseDto> response = memoService.getKeywordMemo(email, keyword, pageable);

//        long end = System.currentTimeMillis();
//        log.info("[searchMemosByTitle] 해당 키워드 포함한 메모 조회 소요 시간: {} ms", (end - start));

        return ResponseEntity.ok(response);
    }
}
