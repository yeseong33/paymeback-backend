package com.paymeback.common.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ErrorResponse {

    private String message;
    private String code;
    private int status;
    private Long timestamp;
    private List<FieldErrorDetail> errors;

    public static ErrorResponse of(ErrorCode errorCode) {
        return ErrorResponse.builder()
            .message(errorCode.getMessage())
            .code(errorCode.getCode())
            .status(errorCode.getHttpStatus().value())
            .timestamp(Instant.now().toEpochMilli())
            .errors(new ArrayList<>())
            .build();
    }

    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return ErrorResponse.builder()
            .message(message)
            .code(errorCode.getCode())
            .status(errorCode.getHttpStatus().value())
            .timestamp(Instant.now().toEpochMilli())
            .errors(new ArrayList<>())
            .build();
    }

    public static ErrorResponse of(ErrorCode errorCode, BindingResult bindingResult) {
        return ErrorResponse.builder()
            .message(errorCode.getMessage())
            .code(errorCode.getCode())
            .status(errorCode.getHttpStatus().value())
            .timestamp(Instant.now().toEpochMilli())
            .errors(FieldErrorDetail.of(bindingResult))
            .build();
    }

    @Getter
    @Builder
    public static class FieldErrorDetail {
        private String field;
        private String value;
        private String reason;

        public static List<FieldErrorDetail> of(BindingResult bindingResult) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            return fieldErrors.stream()
                .map(error -> FieldErrorDetail.builder()
                    .field(error.getField())
                    .value(error.getRejectedValue() == null ? "" : error.getRejectedValue().toString())
                    .reason(error.getDefaultMessage())
                    .build())
                .collect(Collectors.toList());
        }
    }
}