package com.mymemo.backend.memo.service;

import com.mymemo.backend.entity.User;
import com.mymemo.backend.global.exception.CustomException;
import com.mymemo.backend.global.exception.ErrorCode;
import com.mymemo.backend.global.util.SecurityUtil;
import com.mymemo.backend.memo.dto.MemoCreateRequestDto;
import com.mymemo.backend.repository.MemoRepository;
import com.mymemo.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemoService {

    private final UserRepository userRepository;
    private final MemoRepository memoRepository;

    @Transactional
    public void createMemo(MemoCreateRequestDto dto) {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_FOUND));

        memoRepository.save(dto.toEntity(user));
    }
}