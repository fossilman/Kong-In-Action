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
public class ServerException extends BaseException {

    public ServerException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), GlobalErrorResponseConstants.COMMON_SERVER_ERROR_CODE + "",
                GlobalErrorResponseConstants.COMMON_SERVER_ERROR_MESSAGE);
    }

    public ServerException(Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), GlobalErrorResponseConstants.COMMON_SERVER_ERROR_CODE + "",
                GlobalErrorResponseConstants.COMMON_SERVER_ERROR_MESSAGE, cause);
    }

    public ServerException(String code, String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), code, message);
    }

    public ServerException(String code, String message, ErrorLevel errorLevel) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), code, message, errorLevel);
    }

    public ServerException(String code, String message, String developerMessage) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), code, message, developerMessage);
    }

    public ServerException(String code, String message, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), code, message, cause);
    }

    public ServerException(String code, String message, ErrorLevel errorLevel, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), code, message, errorLevel, cause);
    }

    public ServerException(String code, String message, String developerMessage, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), code, message, developerMessage, cause);
    }

    public ServerException(String code, String message, String developerMessage, ErrorLevel errorLevel) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), code, message, developerMessage, errorLevel);
    }

    public ServerException(String code, String message, String developerMessage, ErrorLevel errorLevel, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), code, message, developerMessage, errorLevel, cause);
    }
}
