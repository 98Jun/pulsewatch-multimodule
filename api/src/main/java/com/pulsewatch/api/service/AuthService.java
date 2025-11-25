package com.pulsewatch.api.service;

import com.pulsewatch.api.dto.JoinDTO;

public interface AuthService {
    JoinDTO.joinResponse setMemberJoin(JoinDTO.joinRequest joinRequest);

    Boolean getIdCheck(String id);
}
