package com.mymemo.backend.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    INTERNAL_SERVER_ERROR("서버에 오류가 발생했습니다. 관리자에게 문의하세요.", HttpStatus.INTERNAL_SERVER_ERROR),
    MISSING_PASSWORD("비밀번호를 입력해주세요 .", HttpStatus.BAD_REQUEST),
    MISSING_NICKNAME("닉네임을 입력해주세요.", HttpStatus.BAD_REQUEST),
    MISSING_BIRTHDATE("생년월일을 입력해주세요.", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTS("이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),
    PASSWORD_CONFIRM_MISMATCH("비밀번호와 비밀번호 확인이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_FOUND("존재하지 않는 이메일 주소입니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS("이메일 또는 비밀번호가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_ACCESS("작성자는 필수입니다.", HttpStatus.UNAUTHORIZED),
    EMPTY_MEMO("아무 것도 작성하지 않으셨습니다.", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    MEMO_NOT_FOUND("해당 메모를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ILLEGAL_ARGUMENT("잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_NOT_FOUND("저장된 Refresh Token이 없습니다.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_MISMATCH("요청한 Refresh Token이 저장된 토큰과 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("Refresh Token이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_BLACKLISTED("로그아웃된 토큰입니다.", HttpStatus.UNAUTHORIZED)
    // 필요한 항목 계속 추가 가능
    ;

    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return httpStatus;
    }
}
