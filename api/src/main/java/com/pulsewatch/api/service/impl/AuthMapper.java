package com.pulsewatch.api.service.impl;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthMapper {
    Integer getIdCheck(String id);
}
