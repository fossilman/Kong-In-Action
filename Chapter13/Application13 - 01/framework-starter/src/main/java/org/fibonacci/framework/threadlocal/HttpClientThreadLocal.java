package org.fibonacci.framework.threadlocal;

import com.google.common.collect.Maps;
import org.fibonacci.framework.httpclient.HttpClientTemplate;

import java.util.HashMap;
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
public class HttpClientThreadLocal {

    private static ThreadLocal<Integer> connectTimeout = new ThreadLocal<Integer>();

    private static ThreadLocal<Integer> socketTimeout = new ThreadLocal<Integer>();

    private static ThreadLocal<String> cookieSpec = new ThreadLocal<String>();

    private static ThreadLocal<Boolean> attachedStatisticsParam = new ThreadLocal<Boolean>();

    private static ThreadLocal<Map<String, String>> requestParameters = new ThreadLocal<Map<String, String>>();

    private static ThreadLocal<HttpClientTemplate> httpClient = new ThreadLocal<>();

    private static ThreadLocal<String> requestApi = new ThreadLocal<String>();

    private static ThreadLocal<String> responseCharset = new ThreadLocal<String>();

    private static ThreadLocal<String> traceId = new ThreadLocal<>();

    private static ThreadLocal<String> spanId = new ThreadLocal<>();

    public static String getTraceId() {
        return traceId.get();
    }

    public static void setTraceId(String value) {
        traceId.set(value);
    }

    public static String getSpanId() {
        return spanId.get();
    }

    public static void setSpanId(String value) {
        spanId.set(value);
    }

    public static HttpClientTemplate getHttpClient() {
        return httpClient.get();
    }

    public static void setHttpClient(HttpClientTemplate value) {
        httpClient.set(value);
    }

    public static Integer getConnectTimeout() {
        return connectTimeout.get();
    }

    public static void setConnectTimeout(Integer value) {
        connectTimeout.set(value);
    }

    public static Integer getSocketTimeout() {
        return socketTimeout.get();
    }

    public static void setSocketTimeout(Integer value) {
        socketTimeout.set(value);
    }

    public static String getCookieSpec() {
        return cookieSpec.get();
    }

    public static void setCookieSpec(String value) {
        cookieSpec.set(value);
    }

    public static Boolean getAttachedStatisticsParam() {
        return attachedStatisticsParam.get();
    }

    public static void setAttachedStatisticsParam(Boolean value) {
        attachedStatisticsParam.set(value);
    }

    public static Map<String, String> getRequestParameters() {
        Map<String, String> map = requestParameters.get();
        if (map == null) {
            map = Maps.newHashMap();
            requestParameters.set(map);
        }
        return map;
    }

    public static Map<String, String> getPrintableRequestParameters() {
        return new HashMap<>();
    }

    public static void setRequestParameters(Map<String, String> value) {
        requestParameters.set(value);
    }

    public static String getRequestApi() {
        return requestApi.get();
    }

    public static void setRequestApi(String value) {
        requestApi.set(value);
    }

    public static String getResponseCharset() {
        return responseCharset.get();
    }

    public static void setResponseCharset(String value) {
        responseCharset.set(value);
    }
}
