package com.pulsewatch.api.controller;


import com.pulsewatch.api.dto.JoinDTO;
import com.pulsewatch.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/auth")
public class AuthRController {

    @Autowired
    private AuthService authService;

    @PostMapping("join")
    @Operation(description = "회원가입", summary = "회원가입")
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(schema = @Schema(implementation = JoinDTO.joinResponse.class)))
    public ResponseEntity<JoinDTO.joinResponse> setMemberJoin(@RequestBody JoinDTO.joinRequest joinRequest) {
        JoinDTO.joinResponse result =  authService.setMemberJoin(joinRequest);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/check/id")
    @Operation(description = "아이디 중복체크", summary = "아이디 중복체크")
    public ResponseEntity<Boolean> getIdCheck(@RequestParam
                                              @NotNull
                                              @NotBlank
                                              @Parameter(description = "아이디", example = "test")
                                              String id){
        Boolean result = authService.getIdCheck(id);
        return ResponseEntity.ok(result);
    }

}
