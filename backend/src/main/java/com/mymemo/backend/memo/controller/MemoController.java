package com.mymemo.backend.memo.controller;

import com.mymemo.backend.entity.User;
import com.mymemo.backend.global.util.SecurityUtil;
import com.mymemo.backend.memo.dto.MemoCreateRequestDto;
import com.mymemo.backend.memo.dto.MemoCreateResponseDto;
import com.mymemo.backend.memo.dto.MemoListResponseDto;
import com.mymemo.backend.memo.service.MemoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @GetMapping
    public ResponseEntity<List<MemoListResponseDto>> getAllMemos() {
        long start = System.currentTimeMillis();

        String email = SecurityUtil.getCurrentUserEmail();
        List<MemoListResponseDto> response = memoService.getAllMemos(email);

        long end = System.currentTimeMillis();
        log.info("[getAllMemos] 메모 조회 소요 시간: {} ms", (end - start));

        return ResponseEntity.ok(response);
    }
}
