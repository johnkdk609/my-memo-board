package com.mymemo.backend.memo.controller;

import com.mymemo.backend.global.util.SecurityUtil;
import com.mymemo.backend.memo.dto.*;
import com.mymemo.backend.memo.service.MemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Memo API", description = "메모 관련 API 문서입니다.")
@RestController
@RequestMapping("/api/memos")
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    /**
     * [POST] /api/memos
     * 메모 생성 API
     * @param requestDto 제목, 카테고리, 내용
     * @return 메모 생성 성공 메시지
     */
    @Operation(summary = "메모 생성", description = "새로운 메모를 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "메모 생성 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값 유효성 실패"),
            @ApiResponse(responseCode = "401", description = "로그인 필요")
    })
    @PostMapping
    public ResponseEntity<MemoCreateResponseDto> createMemo(@RequestBody MemoCreateRequestDto requestDto) {
        MemoCreateResponseDto responseDto = memoService.createMemo(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * [GET] /api/memos
     * 페이징 처리된 메모 목록을 조회하는 API
     * @PageableDefault: 기본 페이지 크기와 정렬 기준 지정 (10개씩, 최신순)
     * @ParameterObject: Swagger UI에 pageable 파라미터 자동 반영
     */
    @Operation(summary = "모든 메모 조회", description = "로그인한 사용자의 모든 메모 목록을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요")
    })
    @GetMapping
    public ResponseEntity<PageResponseDto<MemoListResponseDto>> getMemos(
            @ParameterObject @PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
//        long start = System.currentTimeMillis();

        // MemoService를 통해 페이징 처리된 메모 목록 응답을 받음
        PageResponseDto<MemoListResponseDto> response = memoService.getMemos(pageable);

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
    @Operation(summary = "메모 키워드로 검색", description = "키워드를 통해 사용자의 메모 중 일치하는 항목을 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요")
    })
    @GetMapping("/search")
    public ResponseEntity<PageResponseDto<MemoListResponseDto>> searchMemosByTitle(
            @RequestParam String keyword,
            @ParameterObject @PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {

//        long start = System.currentTimeMillis();

        PageResponseDto<MemoListResponseDto> response = memoService.getKeywordMemo(keyword, pageable);

//        long end = System.currentTimeMillis();
//        log.info("[searchMemosByTitle] 해당 키워드 포함한 메모 조회 소요 시간: {} ms", (end - start));

        return ResponseEntity.ok(response);
    }

    /**
     * [GET] /api/memos/{id}
     * 단일 메모 상세 조회 API
     *
     * 로그인한 사용자가 작성한 메모 중, 주어진 ID에 해당하는 메모의 상세 정보를 반환한다.
     * - 삭제된 메모 또는 타인의 메모일 경우 예외가 발생한다.
     *
     * @param id 조회할 메모의 고유 ID (PathVariable)
     * @return MemoDetailResponseDto 메모의 상세 정보 응답 객체
     * @throws CustomException USER_NOT_FOUND: 로그인한 사용자가 존재하지 않을 경우
     * @throws CustomException MEMO_NOT_FOUND: 해당 메모가 없거나 접근 권한이 없는 경우
     */
    @Operation(summary = "단일 메모 상세 조회", description = "로그인한 사용자의 메모 중, 주어진 ID에 해당하는 메모의 상세 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "메모를 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "로그인 필요 (토큰 없음 또는 만료)")
    })
    @Parameter(name = "id", description = "조회할 메모의 ID", required = true)
    @GetMapping("/{id}")
    public ResponseEntity<MemoDetailResponseDto> getMemoById(@PathVariable Long id) {
        MemoDetailResponseDto response = memoService.getMemoDetail(id);

        return ResponseEntity.ok(response);
    }

    /**
     * [PUT] /api/memos/{id}
     * 메모를 수정하는 API
     *
     * @param id 수정할 메모의 ID
     * @param requestDto 수정할 제목, 내용, 카테고리, 공개 여부, 고정 여부 등의 정보
     * @return 수정된 메모에 대한 응답 DTO
     */
    @Operation(summary = "메모 수정", description = "ID에 해당하는 메모를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값 유효성 실패"),
            @ApiResponse(responseCode = "401", description = "로그인 필요"),
            @ApiResponse(responseCode = "404", description = "메모를 찾을 수 없음")
    })
    @Parameter(name = "id", description = "수정할 메모의 ID", required = true)
    @PutMapping("/{id}")
    public ResponseEntity<MemoUpdateResponseDto> updateMemoById(
            @PathVariable Long id,
            @RequestBody MemoUpdateRequestDto requestDto
    ) {
        MemoUpdateResponseDto response = memoService.updateMemo(id, requestDto);

        return ResponseEntity.ok(response);
    }
}
