package com.pulsewatch.api.domain;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@ToString
@Getter
@Setter
public class JoinVO {

    @NotNull
    @NotBlank
    @Parameter(description = "아이디", example = "zxad1234")
    private String id;

    @NotNull
    @NotBlank
    @Parameter(description = "비밀번호", example = "1234")
    private String password;

    @NotNull
    @NotBlank
    @Parameter(description = "이름", example = "박아무개")
    private String name;

    @NotNull
    @NotBlank
    @Parameter(description = "생년월일", example = "1988-12-10")
    private String birthday;

    @NotNull
    @NotBlank
    @Parameter(description = "휴대번호", example = "01044144524")
    private String phoneNumber;
}
