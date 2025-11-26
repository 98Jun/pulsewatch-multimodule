package com.pulsewatch.api.service.impl;

import com.pulsewatch.api.domain.JoinVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthMapper {
    Integer getIdCheck(String id);

    Integer setInsertMemberJoin(JoinVO joinVO);

    Integer getJoinCheck(JoinVO joinVO);
}
