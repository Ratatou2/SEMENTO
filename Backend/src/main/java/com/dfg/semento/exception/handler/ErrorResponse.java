package com.dfg.semento.exception.handler;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.bind.validation.ValidationErrors;
import org.springframework.validation.FieldError;

import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
public class ErrorResponse {
    private final String code;
    private final String message;

    //error가 없으면 응답으로 내려가지 X
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<ValidationError> errors;

    @Getter
    @Setter
    @RequiredArgsConstructor
    @Builder
    public static class ValidationError {
        private final String field;
        private final String message;

        public static ValidationError of(final FieldError fieldError){
            return ValidationError.builder()
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build();
        }
    }
}
