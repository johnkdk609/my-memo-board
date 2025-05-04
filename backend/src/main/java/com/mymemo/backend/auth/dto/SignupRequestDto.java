package com.mymemo.backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class SignupRequestDto {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    private String passwordCheck;

    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    @NotNull(message = "생년월일은 필수입니다.")
    private LocalDate birthDate;

    // 기본 생성자
    public SignupRequestDto() {}

    // 전체 생성자
    public SignupRequestDto(String email, String password, String passwordCheck, String nickname, LocalDate birthDate) {
        this.email = email;
        this.password = password;
        this.passwordCheck = passwordCheck;
        this.nickname = nickname;
        this.birthDate = birthDate;
    }

    // Getter 메서드들
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPasswordCheck() {
        return passwordCheck;
    }

    public String getNickname() {
        return nickname;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }
}
