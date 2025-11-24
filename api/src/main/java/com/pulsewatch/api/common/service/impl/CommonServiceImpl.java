package com.pulsewatch.api.common.service.impl;

import com.pulsewatch.api.common.domain.LogVO;
import com.pulsewatch.api.common.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private CommMapper commMapper;

    @Override
    public Integer setInsertLog(LogVO logVO) {
        return commMapper.setInsertLog(logVO);
    }
}
