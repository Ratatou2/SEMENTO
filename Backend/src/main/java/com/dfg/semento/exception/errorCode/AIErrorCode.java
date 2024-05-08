package com.dfg.semento.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AIErrorCode implements ErrorCode{
    AI_SERVER_NO_RESPONSE(HttpStatus.INTERNAL_SERVER_ERROR, "AI Server Not Response");

    private final HttpStatus httpStatus;
    private final String message;
}
