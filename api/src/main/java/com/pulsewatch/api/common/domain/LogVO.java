package com.pulsewatch.api.common.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
public class LogVO {
    private String method;
    private String uri;
    private String query;
    private int status;
    private String reqBody;
    private String resBody;

    public LogVO(String method, String uri, String query, int status, String reqBody, String resBody) {
        this.method = method;
        this.uri = uri;
        this.query = query;
        this.status = status;
        this.reqBody = reqBody;
        this.resBody = resBody;
    }
}
