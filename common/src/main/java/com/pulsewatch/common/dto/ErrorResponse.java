package com.pulsewatch.common.dto;

/**
 * packageName    : com.pulsewatch.common.dto
 * fileName       : ErrorResponse
 * author         : jun
 * date           : 25. 11. 25.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 11. 25.        jun       최초 생성
 */

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
    private final String code;
    private final String message;
}
