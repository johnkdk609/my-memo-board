package com.mymemo.backend.repository;

import com.mymemo.backend.entity.Memo;
import com.mymemo.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

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

    List<Memo> findAllByUserAndIsDeletedFalseOrderByUpdatedAtDesc(User user);

    // 사용자별 메모 목록을 페이징 + 최신순 정렬로 조회 (삭제되지 않은 메모만 포함)
    Page<Memo> findByUserAndIsDeletedFalseOrderByUpdatedAtDesc(User user, Pageable pageable);

    long countByUser(User user);

    Page<Memo> findByUserAndTitleContainingIgnoreCaseAndIsDeletedFalseOrderByUpdatedAtDesc(User user, String keyword, Pageable pageable);
}