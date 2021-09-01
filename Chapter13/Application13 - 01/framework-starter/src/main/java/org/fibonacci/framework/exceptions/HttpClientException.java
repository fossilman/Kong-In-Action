package org.fibonacci.framework.exceptions;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import org.fibonacci.framework.exceptionhandler.ErrorResponse;
import org.fibonacci.framework.threadlocal.ParameterThreadLocal;
import org.fibonacci.framework.threadlocal.StatisticsThreadLocal;
import org.apache.commons.lang3.StringUtils;

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
public class HttpClientException extends RuntimeException {

    private static final long serialVersionUID = 7753483928731646476L;

    private Integer httpStatusCode;

    private ErrorResponse errorResponse;

    private String url;

    private String errorBody;

    public HttpClientException(Integer httpStatusCode, ErrorResponse errorResponse) {
        this(httpStatusCode, errorResponse, null);
    }

    public HttpClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpClientException(String message) {
        super(message);
    }

    public HttpClientException(Integer httpStatusCode, ErrorResponse errorResponse, String url, String errorBody) {
        this(httpStatusCode, errorResponse, url);
        this.errorBody = errorBody;
    }

    public HttpClientException(Integer httpStatusCode, ErrorResponse errorResponse, String url) {
        super(getMessage(null, errorResponse, url));
        this.httpStatusCode = httpStatusCode;
        this.errorResponse = errorResponse;
        this.url = url;
    }

    public HttpClientException(String message, Throwable cause, String url, String errorBody) {
        this(message, cause, url);
        this.errorBody = errorBody;
    }

    public HttpClientException(String message, Throwable cause, String url) {
        super(getMessage(message, null, url), cause);
        this.url = url;
    }

    public HttpClientException(String message, String url) {
        super(getMessage(message, null, url));
        this.url = url;
    }

    public String getErrorBody() {
        return errorBody;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }

    private static String extractApi(String url) {
        if (StringUtils.isEmpty(url)) {
            return url;
        } else {
            int sepIndex = url.indexOf("?");
            if (sepIndex < 0) {
                return url;
            } else {
                return url.substring(0, sepIndex);
            }
        }
    }

    private static String getMessage(String message, ErrorResponse errorResponse, String url) {
        Map<String, Object> messageMap = Maps.newHashMap();
        messageMap.put("requestApi", StatisticsThreadLocal.getApiName());
        messageMap.put("requestId", ParameterThreadLocal.getRequestId());
        messageMap.put("message", message);
        messageMap.put("httpOutUrl", extractApi(url));
        if (errorResponse != null) {
            messageMap.putAll(errorResponse.toMap());
        }
        return JSON.toJSONString(messageMap);
    }

    public String getUrl() {
        return extractApi(url);
    }
}

