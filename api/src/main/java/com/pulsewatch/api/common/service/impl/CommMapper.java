package com.pulsewatch.api.common.service.impl;

import com.pulsewatch.api.common.domain.LogVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommMapper {

    Integer setInsertLog(LogVO logVO);
}
