package com.damm.server.global.error;

import lombok.Getter;

@Getter
public class DammException extends RuntimeException {
    private final ErrorCode errorCode;

    public DammException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}