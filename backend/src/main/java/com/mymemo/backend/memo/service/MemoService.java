package com.mymemo.backend.memo.service;

import com.mymemo.backend.entity.Memo;
import com.mymemo.backend.entity.User;
import com.mymemo.backend.entity.enums.Visibility;
import com.mymemo.backend.global.exception.CustomException;
import com.mymemo.backend.global.exception.ErrorCode;
import com.mymemo.backend.global.util.SecurityUtil;
import com.mymemo.backend.memo.dto.*;
import com.mymemo.backend.repository.MemoRepository;
import com.mymemo.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * Memo 엔티티 페이지 객체를 MemoListResponseDto로 변환하고,
     * 커스텀 PageResponseDto로 래핑하여 반환한다.
     *
     * @param memoPage Memo 엔티티 페이지 객체
     * @return 변환된 PageResponseDto<MemoListResponseDto>
     */
    private PageResponseDto<MemoListResponseDto> toPageResponse(Page<Memo> memoPage) {
        // Memo 엔티티 MemoListResponseDto로 매핑
        Page<MemoListResponseDto> dtoPage = memoPage.map(MemoListResponseDto::from);
        // 커스텀 Page 응답 객체로 변환하여 반환
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
     * @param pageable 페이징 및 정렬 정보를 포함한 객체
     * @return PageResponseDto<MemoListResponseDto> 응답 DTO로 감싼 페이징 결과
     */
    @Transactional(readOnly = true)
    public PageResponseDto<MemoListResponseDto> getMemos(Pageable pageable) {
        // 현재 로그인한 사용자 이메일 확인
        String email = SecurityUtil.getCurrentUserEmail();

        // 사용자 조회 (없는 경우 예외 발생)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 삭제되지 않은 메모들을 최신순으로 페이징 조회
        Page<Memo> memoPage = memoRepository.findByUserAndIsDeletedFalseOrderByIsPinnedDescPinOrderAscUpdatedAtDesc(user, pageable);

        return toPageResponse(memoPage);
    }

    /**
     * 페이징 처리된, 키워드를 제목에서 검색한 메모 목록 조회
     * @param keyword 검색할 키워드 (제목 기준)
     * @param pageable 페이징 및 정렬 정보를 포함한 객체
     * @return PageResponseDto<MemoListResponseDto> 응답 DTO로 감싼 페이징 결과
     */
    @Transactional(readOnly = true)
    public PageResponseDto<MemoListResponseDto> getKeywordMemo(String keyword, Pageable pageable) {
        // 현재 로그인한 사용자 이메일 확인
        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Page<Memo> memoPage = memoRepository.findByUserAndTitleContainingIgnoreCaseAndIsDeletedFalseOrderByIsPinnedDescPinOrderAscUpdatedAtDesc(user, keyword, pageable);

        return toPageResponse(memoPage);
    }

    /**
     * 특정 ID의 메모를 상세 조회
     *
     * - 로그인한 사용자의 메모인 경우: 무조건 조회 가능
     * - 비회원 또는 타인의 메모인 경우: PUBLIC 메모만 조회 가능
     * - 삭제된 메모이거나 접근 권한이 없을 경우 예외 발생
     *
     * @param memoId 조회할 메모의 고유 ID
     * @return 메모의 상세 정보를 담은 MemoDetailResponseDto
     * @throws CustomException MEMO_NOT_FOUND: 메모가 존재하지 않거나 삭제된 경우
     * @throws CustomException MEMO_PRIVATE_ACCESS_DENIED: 비공개 메모에 대한 접근 시도
     */
    @Transactional(readOnly = true)
    public MemoDetailResponseDto getMemoDetail(Long memoId) {
        // 메모 존재 여부 및 삭제 여부 확인
        Memo memo = memoRepository.findByIdAndIsDeletedFalse(memoId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMO_NOT_FOUND));

        String currentUserEmail = SecurityUtil.getCurrentUserEmail();   // 로그인 여부

        // 소유자인 경우 -> 바로 반환
        if (currentUserEmail != null && memo.getUser().getEmail().equals(currentUserEmail)) {
            return new MemoDetailResponseDto(memo);
        }

        // 비회원 또는 타인의 경우 -> PUBLIC 메모만 조회 가능
        if (memo.getVisibility() != Visibility.PUBLIC) {
            throw new CustomException(ErrorCode.MEMO_PRIVATE_ACCESS_DENIED);
        }

        return new MemoDetailResponseDto(memo);
    }

    @Transactional
    public MemoUpdateResponseDto updateMemo(Long memoId, MemoUpdateRequestDto requestDto) {
        String email = SecurityUtil.getCurrentUserEmail();

        // 현재 로그인한 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 메모 조회 (작성자 본인의 메모인지 확인)
        Memo memo = memoRepository.findByIdAndUserAndIsDeletedFalse(memoId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMO_NOT_FOUND));

        // pinned 상태 변경 여부 판단
        boolean wasPinned = memo.isPinned();
        boolean nowPinned = requestDto.isPinned();

        // 메모 수정
        memo.update(
                requestDto.getTitle(),
                requestDto.getContent(),
                requestDto.getMemoCategory(),
                requestDto.getVisibility(),
                nowPinned
        );

        // pinned 상태 변경이 있었다면 pinOrder 업데이트
        if (!wasPinned && nowPinned) {
            // 새로 고정되었을 경우 -> 최대 pinOrder + 1 부여
            int minPinOrder = memoRepository.findMinPinOrderByUser(user);
            int newPinOrder = (minPinOrder <= Integer.MIN_VALUE + 1) ? Integer.MIN_VALUE : minPinOrder - 1;     // 극단적인 케이스 처리
            memo.updatePinOrder(newPinOrder);
        } else if (wasPinned && !nowPinned) {
            // 고정 해제될 경우 -> pinOrder 초기화
            memo.updatePinOrder(0);
        }

        // 응답 반환
        return new MemoUpdateResponseDto(memo);
    }

    @Transactional
    public void deleteMemo(Long memoId) {
        // 1. 현재 로그인한 사용자 이메일 확인
        String email = SecurityUtil.getCurrentUserEmail();

        // 2. 사용자 정보 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 3. 삭제되지 않은 메모인지 확인하며 조회
        Memo memo = memoRepository.findByIdAndUserAndIsDeletedFalse(memoId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMO_NOT_FOUND));

        // 4. soft delete 처리
        memo.softDelete();
    }

    // 읽기 전용 트랜잭션은 flush가 발생하지 않고, 엔티티 변경 감지를 하지 않아 성능상 유리하다.
    // 이는 데이터 변경이 없는 메서드에만 사용하는 것이 바람직하다.
    // 읽기 전용 트랜잭션은 스냅샷 저장, 더티체킹 등을 생략하기 때문에 약간 더 가볍고 빠르다. 그리고 데이터 변경을 하지 않는다는 점 명시.
    @Transactional(readOnly = true)
    public MemoDetailResponseDto getMemoDetailByUuid(String uuid) {
        Memo memo = memoRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMO_NOT_FOUND));

        if (memo.getVisibility() != Visibility.PUBLIC) {
            throw new CustomException(ErrorCode.MEMO_PRIVATE_ACCESS_DENIED);
        }

        return new MemoDetailResponseDto(memo);
    }
}