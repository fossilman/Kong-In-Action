package org.fibonacci.framework.exceptionhandler;

import com.google.common.collect.Maps;
import lombok.Data;

import java.util.Map;

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
@Data
public class ErrorResponse {

    private int status;
    private String code;
    private String message;
    private String developerMessage;
    private String errorLevel;

    public ErrorResponse() {
    }

    public ErrorResponse(int status, String code, String message, String developerMessage) {

        super();
        this.status = status;
        this.code = code;
        this.message = message;
        this.developerMessage = developerMessage;
    }

    public ErrorResponse(int status, String code, String message, String developerMessage, String errorLevel) {

        super();
        this.status = status;
        this.code = code;
        this.message = message;
        this.developerMessage = developerMessage;
        this.errorLevel = errorLevel;
    }


    public Map<String, Object> toMap() {

        Map<String, Object> map = Maps.newHashMap();
        map.put("status", status);
        map.put("code", code);
        map.put("message", message);
        map.put("developerMessage", developerMessage);
        map.put("errorLevel", errorLevel);
        return map;
    }

}
