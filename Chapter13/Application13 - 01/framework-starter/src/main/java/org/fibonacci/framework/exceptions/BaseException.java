package org.fibonacci.framework.exceptions;

import lombok.Getter;

/**
 * <p>
 * Copyright (C) 2020 Shanghai LuoJin Com., Ltd. All rights reserved.
 * <p>
 * No parts of this file may be reproduced or transmitted in any form or by any means,
 * electronic, mechanical, photocopying, recording, or otherwise, without prior written
 * permission of Shanghai LuoJin Com., Ltd.
 *
 * @author krame
 * @date 2020/11/25
 */
@Getter
public class BaseException extends RuntimeException {

    private int httpStatus;
    private String code;
    private String developerMessage;
    private String errorLevel = ErrorLevel.FATAL.getValue();

    public BaseException(int httpStatus, String code, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = code;
    }

    public BaseException(int httpStatus, String code, String message, ErrorLevel errorLevel) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = code;
        this.errorLevel = errorLevel.getValue();
    }

    public BaseException(int httpStatus, String code, String message, String developerMessage) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = code;
        this.developerMessage = developerMessage;
    }

    public BaseException(int httpStatus, String code, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.code = code;
    }

    public BaseException(int httpStatus, String code, String message, ErrorLevel errorLevel, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.code = code;
        this.errorLevel = errorLevel.getValue();
    }

    public BaseException(int httpStatus, String code, String message, String developerMessage, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.code = code;
        this.developerMessage = developerMessage;
    }

    public BaseException(int httpStatus, String code, String message, String developerMessage, ErrorLevel errorLevel) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = code;
        this.developerMessage = developerMessage;
        this.errorLevel = errorLevel.getValue();
    }

    public BaseException(int httpStatus, String code, String message, String developerMessage, ErrorLevel errorLevel, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.code = code;
        this.developerMessage = developerMessage;
        this.errorLevel = errorLevel.getValue();
    }

    public enum ErrorLevel {
        WEAK("-1"), NORMAL("0"), FATAL("1");
        private String value;
        ErrorLevel(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

}
