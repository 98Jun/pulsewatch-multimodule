package com.pulsewatch.api.dto;

import com.pulsewatch.api.domain.JoinVO;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class JoinDTO {



    @Getter
    @Setter
    @ToString
    @Builder
    public static class joinRequest{
        @NotNull
        @NotBlank
        @Schema(description = "아이디", example = "zxad1234")
        private String id;

        @NotNull
        @NotBlank
        @Schema(description = "비밀번호", example = "1234")
        private String password;

        @NotNull
        @NotBlank
        @Schema(description = "이름", example = "박아무개")
        private String name;

        @NotNull
        @NotBlank
        @Schema(description = "생년월일", example = "1988-12-10")
        private String birthDay;

        @NotNull
        @NotBlank
        @Schema(description = "휴대번호", example = "01044144524")
        private String phoneNumber;

        public static JoinVO convertJoinVO(JoinDTO.joinRequest joinRequest){
            return JoinVO.builder()
                    .id(joinRequest.getId())
                    .birthDay(joinRequest.getBirthDay())
                    .phoneNumber(joinRequest.getPhoneNumber())
                    .password(joinRequest.getPassword())
                    .name(joinRequest.getName())
                    .build();
        }
    }

}
