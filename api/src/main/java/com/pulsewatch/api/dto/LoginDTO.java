package com.pulsewatch.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * packageName    : com.pulsewatch.api.dto
 * fileName       : LoginDTO
 * author         : jun
 * date           : 25. 11. 26.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 11. 26.        jun       최초 생성
 */
public class LoginDTO {

    @Getter
    @Setter
    @ToString
    public static class loginRequest{
        @NotNull
        @NotBlank
        @Schema(description = "아이디", example = "zxad1234")
        private String id;

        @NotNull
        @NotBlank
        @Schema(description = "비밀번호", example = "1234")
        private String password;
    }

    @Getter
    @Setter
    @Builder
    public static class loginResponse{
        private String id;
        private String accessToken;
        private String refreshToken;
    }
}
