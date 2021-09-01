//package org.fibonacci.framework.threadlocal;
//
//import org.fibonacci.framework.filter.log.HttpInvokeLogUtil;
//import org.fibonacci.framework.filter.log.RequestWrapper;
//import org.apache.commons.lang3.StringUtils;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.Map;
//
///**
// * <p>
// * Copyright (C) 2020 Shanghai LuoJin Com., Ltd. All rights reserved.
// * <p>
// * No parts of this file may be reproduced or transmitted in any form or by any means,
// * electronic, mechanical, photocopying, recording, or otherwise, without prior written
// * permission of Shanghai LuoJin Com., Ltd.
// *
// * @author krame
// * @date 2020/11/25
// */
//public class RequestThreadLocal {
//
//    private static ThreadLocal<String> requestUri = new ThreadLocal<String>();
//    private static ThreadLocal<String> requestMethod = new ThreadLocal<String>();
//    private static ThreadLocal<Map<String, String[]>> requestParameters = new ThreadLocal<Map<String, String[]>>();
//    private static ThreadLocal<String> requestBody = new ThreadLocal<String>();
//    private static ThreadLocal<RequestWrapper> requestWrapper = new ThreadLocal<RequestWrapper>();
//
//    public static String getRequestUri() {
//        return requestUri.get();
//    }
//
//    public static String getPrintableRequestUri() {
//        return HttpInvokeLogUtil.getPrintableRequestUri(requestUri.get());
//    }
//
//    public static void setRequestUri(String value) {
//        requestUri.set(value);
//    }
//
//    public static String getRequestMethod() {
//        return requestMethod.get();
//    }
//
//    public static void setRequestMethod(String value) {
//        requestMethod.set(value);
//    }
//
//    public static Map<String, String[]> getRequestParameters() {
//        return requestParameters.get();
//    }
//
//    public static Map<String, String[]> getPrintableRequestParameters() {
//        return HttpInvokeLogUtil.getPrintableRequestParameters(requestParameters.get());
//    }
//
//    public static void setRequestParameters(Map<String, String[]> value) {
//        requestParameters.set(value);
//    }
//
//    public static String getRequestBody() throws IOException {
//        RequestWrapper requestWrapperContent = requestWrapper.get();
//        if (requestWrapperContent != null) {
//            ByteArrayOutputStream logOutputStream = requestWrapperContent.getLogOutputStream();
//            if (logOutputStream != null) {
//                String outputStreamToString = outputStreamToString(logOutputStream);
//                if (StringUtils.isNotBlank(outputStreamToString)) {
//                    setRequestBody(outputStreamToString);
//                }
//            }
//        }
//        return requestBody.get();
//    }
//
//    private static void setRequestBody(String value) {
//        requestBody.set(value);
//    }
//
//    public static RequestWrapper getRequestWrapper() {
//        return requestWrapper.get();
//    }
//
//    public static void setRequestWrapper(RequestWrapper value) {
//        requestWrapper.set(value);
//    }
//
//    public static void clear() {
//        requestUri.set(null);
//        requestMethod.set(null);
//        requestParameters.set(null);
//        requestBody.set(null);
//        requestWrapper.set(null);
//    }
//
//    private static String outputStreamToString(ByteArrayOutputStream outputStream) throws IOException {
//        String str = null;
//        try {
//            str = outputStream.toString("UTF-8");
//        } finally {
//            outputStream.close();
//        }
//        return str;
//    }
//}
