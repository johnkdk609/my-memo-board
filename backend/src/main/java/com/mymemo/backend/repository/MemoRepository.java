package com.mymemo.backend.repository;

import com.mymemo.backend.entity.Memo;
import com.mymemo.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemoRepository extends JpaRepository<Memo, Long> {

    /**
     * 이 인터페이스를 선언하는 것만으로도 Spring Data JPA는 다음과 같은 메서드를 자동으로 제공한다.
     *
     * save(Memo memo)      메모 저장 (INSERT or UPDATE)
     * findById(Long id)    메모 단건 조회
     * findAll()            전체 메모 목록 조회
     * deleteById(Long id)  메모 삭제
     * count()              메모 개수 조회
     * existsById(Long id)  메모 존재 여부 확인
     */

    // 해당 사용자의 모든 메모를 최신순으로 조회 (삭제되지 않은 메모만 포함, 페이징 없음) - 현재 사용 X
    List<Memo> findAllByUserAndIsDeletedFalseOrderByUpdatedAtDesc(User user);

    // 해당 사용자의 메모를 페이징 처리하여 최신순으로 조회 (삭제되지 않은 메모만 포함)
    Page<Memo> findByUserAndIsDeletedFalseOrderByIsPinnedDescPinOrderAscUpdatedAtDesc(User user, Pageable pageable);

    // 해당 사용자가 작성한 전체 메모 개수를 반환 (삭제 여부와 무관)
    long countByUser(User user);

    /**
     * 특정 사용자의 메모 중 삭제되지 않았고, 제목에 키워드가 포함된 메모들을 최신순으로 페이징 조회
     * @param user 조회 대상 사용자
     * @param keyword 검색 키워드 (제목 기준, 대소문자 무시)
     * @param pageable 페이징 및 정렬 정보
     * @return 키워드가 포함된 메모 페이지 객체
     */
    Page<Memo> findByUserAndTitleContainingIgnoreCaseAndIsDeletedFalseOrderByIsPinnedDescPinOrderAscUpdatedAtDesc(User user, String keyword, Pageable pageable);

    Optional<Memo> findByIdAndUserAndIsDeletedFalse(Long id, User user);

    @Query("SELECT COALESCE(MIN(m.pinOrder), 0) FROM Memo AS m WHERE m.user = :user AND m.isPinned = true AND m.isDeleted = false")
    int findMinPinOrderByUser(@Param("user") User user);
}