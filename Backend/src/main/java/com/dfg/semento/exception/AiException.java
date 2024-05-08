package com.dfg.semento.exception;

import com.dfg.semento.exception.errorCode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AiException extends RuntimeException {
    private final ErrorCode aiErrorCode;
}