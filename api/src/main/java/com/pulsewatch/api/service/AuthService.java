package com.pulsewatch.api.service;

import com.pulsewatch.api.dto.JoinDTO;
import com.pulsewatch.api.dto.LoginDTO;

public interface AuthService {
    LoginDTO.loginResponse setMemberJoin(JoinDTO.joinRequest joinRequest);

    Boolean getIdCheck(String id);

    LoginDTO.loginResponse getLogin(LoginDTO.loginRequest loginRequest);
}
