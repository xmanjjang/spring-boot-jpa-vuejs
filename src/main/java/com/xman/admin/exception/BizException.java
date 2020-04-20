package com.xman.admin.exception;

import com.xman.admin.constants.SystemStatusCode;
import lombok.Getter;

@Getter
public class BizException extends RuntimeException {
    private SystemStatusCode code;
    private String message;

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizException(String message) {
        super(message);
        this.message = message;
    }

    public BizException(SystemStatusCode statusCode, String message) {
        this.code = statusCode;
        this.message = message;
    }
}
