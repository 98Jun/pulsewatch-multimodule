package com.pulsewatch.common.error;

/**
 * packageName    : com.pulsewatch.common.error
 * fileName       : GlobalExceptionHandler
 * author         : jun
 * date           : 25. 11. 25.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 11. 25.        jun       최초 생성
 */

import com.pulsewatch.common.dto.ErrorResponse;
import com.pulsewatch.common.error.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {

//        log.warn("[BusinessException] code={}, message={}", ex.getErrorCode(), ex.getMessage());

        ErrorResponse body = ErrorResponse.builder()
                .code(ex.getErrorCode())
                .message(ex.getMessage())
                .build();

        // 여기서는 일단 전부 400으로 응답, 필요하면 errorCode별로 분기해서 409/401 등 매핑
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {

//        log.error("[Exception] 예상치 못한 에러", ex);

        ErrorResponse body = ErrorResponse.builder()
                .code("INTERNAL_SERVER_ERROR")
                .message("알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.")
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }
}