package com.mymemo.backend.memo.service;

import com.mymemo.backend.entity.Memo;
import com.mymemo.backend.entity.User;
import com.mymemo.backend.global.exception.CustomException;
import com.mymemo.backend.global.exception.ErrorCode;
import com.mymemo.backend.global.util.SecurityUtil;
import com.mymemo.backend.memo.dto.MemoCreateRequestDto;
import com.mymemo.backend.memo.dto.MemoCreateResponseDto;
import com.mymemo.backend.memo.dto.MemoListResponseDto;
import com.mymemo.backend.memo.dto.PageResponseDto;
import com.mymemo.backend.repository.MemoRepository;
import com.mymemo.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoService {

    private final UserRepository userRepository;
    private final MemoRepository memoRepository;

    @Transactional
    public MemoCreateResponseDto createMemo(MemoCreateRequestDto dto) {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_FOUND));

        Memo memo = memoRepository.save(dto.toEntity(user));
        return new MemoCreateResponseDto(memo);
    }

    private PageResponseDto<MemoListResponseDto> toPageResponse(Page<Memo> memoPage) {
        // 엔티티 -> DTO 매핑
        Page<MemoListResponseDto> dtoPage = memoPage.map(MemoListResponseDto::from);
        // 커스텀 Page 응답 DTO에 필요한 정보 구성
        return new PageResponseDto<>(
                dtoPage.getContent(),       // 현재 페이지의 데이터 리스트
                dtoPage.getNumber(),        // 현재 페이지 번호
                dtoPage.getSize(),          // 페이지당 개수
                dtoPage.getTotalElements(), // 전체 항목 수
                dtoPage.getTotalPages(),    // 전체 페이지 수
                dtoPage.isLast()            // 마지막 페이지 여부
        );
    }

    /**
     * 페이징 처리된 메모 목록을 조회
     * @param email 현재 로그인한 사용자 이메일
     * @param pageable 페이징 및 정렬 정보를 포함한 객체
     * @return PageResponseDto<MemoListResponseDto> 응답 DTO로 감싼 페이징 결과
     */
    public PageResponseDto<MemoListResponseDto> getMemos(String email, Pageable pageable) {
        // 사용자 조회 (없는 경우 예외 발생)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 삭제되지 않은 메모들을 최신순으로 페이징 조회
        Page<Memo> memoPage = memoRepository.findByUserAndIsDeletedFalseOrderByUpdatedAtDesc(user, pageable);

        return toPageResponse(memoPage);
    }

    /**
     * 페이징 처리된, 키워드를 제목에서 검색한 메모 목록 조회
     * @param email 현재 로그인한 사용자 이메일
     * @param keyword 검색할 키워드 (제목 기준)
     * @param pageable 페이징 및 정렬 정보를 포함한 객체
     * @return PageResponseDto<MemoListResponseDto> 응답 DTO로 감싼 페이징 결과
     */
    public PageResponseDto<MemoListResponseDto> getKeywordMemo(String email, String keyword, Pageable pageable) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Page<Memo> memoPage = memoRepository.findByUserAndTitleContainingIgnoreCaseAndIsDeletedFalseOrderByUpdatedAtDesc(user, keyword, pageable);

        return toPageResponse(memoPage);
    }
}