package com.mymemo.backend.entity;

import com.mymemo.backend.global.exception.CustomException;
import com.mymemo.backend.global.exception.ErrorCode;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;   // 이메일은 로그인 및 식별의 기준이 되므로 고유해야 함

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected User() {}     // protected를 하여 JPA는 호출 가능, 외부 코드에서는 직접 호출 제한됨 (불필요한 객체 생성 가능성 방지)

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public User(String password, String nickname, String email, LocalDate birthDate) {
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.birthDate = birthDate;
    }

    public void changePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new CustomException(ErrorCode.MISSING_PASSWORD);
        }
        this.password = password;
    }

    public void changeNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new CustomException(ErrorCode.MISSING_NICKNAME);
        }
        this.nickname = nickname;
    }

    public void changeBirthDate(LocalDate birthDate) {
        if (birthDate == null) {
            // 생일은 회원가입 이후 변경 가능하지만, null로 바꾸는 것은 금지
            throw new CustomException(ErrorCode.MISSING_BIRTHDATE);
        }
        this.birthDate = birthDate;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() { return password; }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}
