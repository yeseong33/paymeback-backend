package com.paymeback.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C002", "잘못된 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C003", "허용되지 않은 HTTP 메서드입니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C004", "엔티티를 찾을 수 없습니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C005", "잘못된 타입 값입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "C006", "접근이 거부되었습니다."),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "C007", "요청이 너무 많습니다. 잠시 후 다시 시도해주세요."),

    // Authentication & Authorization
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "A001", "인증에 실패했습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "만료된 토큰입니다."),
    INSUFFICIENT_PERMISSION(HttpStatus.FORBIDDEN, "A004", "권한이 부족합니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U002", "이미 존재하는 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U003", "비밀번호가 일치하지 않습니다."),
    USER_NOT_VERIFIED(HttpStatus.FORBIDDEN, "U004", "이메일 인증이 완료되지 않았습니다."),

    // OTP
    INVALID_OTP(HttpStatus.BAD_REQUEST, "O001", "유효하지 않은 OTP입니다."),
    EXPIRED_OTP(HttpStatus.BAD_REQUEST, "O002", "만료된 OTP입니다."),
    OTP_NOT_FOUND(HttpStatus.NOT_FOUND, "O003", "OTP를 찾을 수 없습니다."),

    // Gathering
    GATHERING_NOT_FOUND(HttpStatus.NOT_FOUND, "G001", "모임을 찾을 수 없습니다."),
    GATHERING_ALREADY_CLOSED(HttpStatus.BAD_REQUEST, "G002", "이미 종료된 모임입니다."),
    QR_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "G003", "QR코드가 만료되었습니다."),
    ALREADY_PARTICIPATED(HttpStatus.CONFLICT, "G004", "이미 참여한 모임입니다."),
    NOT_GATHERING_OWNER(HttpStatus.FORBIDDEN, "G005", "모임 방장이 아닙니다."),
    GATHERING_NOT_READY_FOR_PAYMENT(HttpStatus.BAD_REQUEST, "G006", "결제 요청을 생성할 수 없는 상태입니다."),

    // Expense
    EXPENSE_NOT_FOUND(HttpStatus.NOT_FOUND, "E001", "지출 내역을 찾을 수 없습니다."),
    NOT_EXPENSE_OWNER(HttpStatus.FORBIDDEN, "E002", "지출 등록자가 아닙니다."),

    // Settlement
    SETTLEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "정산 정보를 찾을 수 없습니다."),
    NOT_SETTLEMENT_SENDER(HttpStatus.FORBIDDEN, "S002", "정산 송금자가 아닙니다."),
    NOT_SETTLEMENT_RECEIVER(HttpStatus.FORBIDDEN, "S003", "정산 수령자가 아닙니다."),
    SETTLEMENT_ALREADY_PROCESSED(HttpStatus.CONFLICT, "S004", "이미 처리된 정산입니다."),
    SETTLEMENT_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "S005", "완료되지 않은 정산입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}