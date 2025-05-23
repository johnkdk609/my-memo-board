package com.mymemo.backend.memo.service;

import com.mymemo.backend.entity.Memo;
import com.mymemo.backend.entity.User;
import com.mymemo.backend.global.exception.CustomException;
import com.mymemo.backend.global.exception.ErrorCode;
import com.mymemo.backend.global.util.SecurityUtil;
import com.mymemo.backend.memo.dto.MemoCreateRequestDto;
import com.mymemo.backend.memo.dto.MemoCreateResponseDto;
import com.mymemo.backend.memo.dto.MemoListResponseDto;
import com.mymemo.backend.repository.MemoRepository;
import com.mymemo.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    public List<MemoListResponseDto> getAllMemos(User user) {
        List<Memo> memos = memoRepository.findAllByUserAndIsDeletedFalseOrderByUpdatedAtDesc(user);
        List<MemoListResponseDto> responseList = new ArrayList<>();

        for (Memo memo : memos) {
            responseList.add(new MemoListResponseDto(memo));
        }

        return responseList;
    }
}