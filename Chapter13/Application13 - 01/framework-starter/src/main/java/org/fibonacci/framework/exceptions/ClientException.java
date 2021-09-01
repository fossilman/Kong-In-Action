package org.fibonacci.framework.exceptions;

import org.fibonacci.framework.exceptionhandler.GlobalErrorResponseConstants;
import org.springframework.http.HttpStatus;

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
public class ClientException extends BaseException {

    public ClientException() {
        super(HttpStatus.UNPROCESSABLE_ENTITY.value(), GlobalErrorResponseConstants.COMMON_CLIENT_ERROR_CODE + "",
                GlobalErrorResponseConstants.COMMON_CLIENT_ERROR_MESSAGE);
    }

    public ClientException(Throwable cause) {
        super(HttpStatus.UNPROCESSABLE_ENTITY.value(), GlobalErrorResponseConstants.COMMON_CLIENT_ERROR_CODE + "",
                GlobalErrorResponseConstants.COMMON_CLIENT_ERROR_MESSAGE, cause);
    }

    public ClientException(int httpStatus) {
        super(httpStatus, GlobalErrorResponseConstants.COMMON_CLIENT_ERROR_CODE + "", GlobalErrorResponseConstants.COMMON_CLIENT_ERROR_MESSAGE);
    }

    public ClientException(int httpStatus, String code, String message) {
        super(httpStatus, code, message);
    }

    public ClientException(int httpStatus, String code, String message, Throwable cause) {
        super(httpStatus, code, message, cause);
    }

    public ClientException(String code, String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY.value(), code, message);
    }

    public ClientException(String code, String message, ErrorLevel errorLevel) {
        super(HttpStatus.UNPROCESSABLE_ENTITY.value(), code, message, errorLevel);
    }

    public ClientException(String code, String message, String developerMessage) {
        super(HttpStatus.UNPROCESSABLE_ENTITY.value(), code, message, developerMessage);
    }

    public ClientException(String code, String message, Throwable cause) {
        super(HttpStatus.UNPROCESSABLE_ENTITY.value(), code, message, cause);
    }

    public ClientException(String code, String message, ErrorLevel errorLevel, Throwable cause) {
        super(HttpStatus.UNPROCESSABLE_ENTITY.value(), code, message, errorLevel, cause);
    }

    public ClientException(String code, String message, String developerMessage, Throwable cause) {
        super(HttpStatus.UNPROCESSABLE_ENTITY.value(), code, message, developerMessage, cause);
    }

    public ClientException(String code, String message, String developerMessage, ErrorLevel errorLevel) {
        super(HttpStatus.UNPROCESSABLE_ENTITY.value(), code, message, developerMessage, errorLevel);
    }

    public ClientException(String code, String message, String developerMessage, ErrorLevel errorLevel, Throwable cause) {
        super(HttpStatus.UNPROCESSABLE_ENTITY.value(), code, message, developerMessage, errorLevel, cause);
    }
}
