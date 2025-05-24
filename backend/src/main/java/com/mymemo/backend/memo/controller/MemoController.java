package com.mymemo.backend.memo.controller;

import com.mymemo.backend.entity.User;
import com.mymemo.backend.global.util.SecurityUtil;
import com.mymemo.backend.memo.dto.MemoCreateRequestDto;
import com.mymemo.backend.memo.dto.MemoCreateResponseDto;
import com.mymemo.backend.memo.dto.MemoListResponseDto;
import com.mymemo.backend.memo.service.MemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        String email = SecurityUtil.getCurrentUserEmail();
        List<MemoListResponseDto> response = memoService.getAllMemos(email);
        return ResponseEntity.ok(response);
    }
}
