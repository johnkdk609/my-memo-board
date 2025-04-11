package com.mymemo.backend;

import jakarta.persistence.*;
import java.time.LocalDate;

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
    private LocalDate createdAt;

    protected User() {}

    public User(String password, String nickname, String email, LocalDate birthDate) {
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.birthDate = birthDate;
        this.createdAt = LocalDate.now();
    }

    public void changePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 비어있을 수 없습니다.");
        }
        this.password = password;
    }

    public void changeNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("닉네임은 비어있을 수 없습니다.");
        }
        this.nickname = nickname;
    }

    public void changeBirthDate(LocalDate birthDate) {
        if (birthDate == null) {
            // 생일은 회원가입 이후 변경 가능하지만, null로 바꾸는 것은 금지
            throw new IllegalArgumentException("생년월일은 null일 수 없습니다.");
        }
        this.birthDate = birthDate;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }
}
