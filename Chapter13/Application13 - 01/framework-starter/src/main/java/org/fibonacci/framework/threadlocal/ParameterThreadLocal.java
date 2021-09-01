package org.fibonacci.framework.threadlocal;

import com.google.common.collect.Maps;

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
public class ParameterThreadLocal {

    /* PARAM_OS 表示操作系统信息，相对请求中的p_o参数 */
    public static final String PARAM_OS = "os";
    /* PARAM_UID 表示用户ID，相对请求中的p_u参数 */
    public static final String PARAM_UID = "uid";
    /* PARAM_NAME 表示用户拼音，相对请求中的p_n参数 */
    private static final String PARAM_NAME = "name";
    /* PARAM_NAME 表示用户拼音，相对请求中的p_c参数 */
    private static final String PARAM_ACCOUNT = "account";
    /* PARAM_NAME 表示用户拼音，相对请求中的p_c参数 */
    private static final String PARAM_AUTH_TYPE = "auth_type";
    /* PARAM_SESSION_ID 表示 SessionId，相对请求中的p_s参数 */
    public static final String PARAM_SESSION_ID = "sessionId";
    /* PARAM_TOKEN 表示 JWT Token，相对请求中的p_t参数 */
    public static final String PARAM_TOKEN = "token";
    /* PARAM_REQUEST_ID 表示请求中的 requestId，用于追踪调用链，相对请求中的r_i参数 */
    public static final String PARAM_REQUEST_ID = "requestId";
    /* PARAM_CURRENT_HOP 表示当前请求的 Step */
    public static final String PARAM_CURRENT_HOP = "currentHop";
    /* PARAM_TIMESTAMP 表示请求的时间戳 */
    public static final String PARAM_TIMESTAMP = "timestamp";
    /* PARAM_REMOTE_ADDR 表示远程调用地址，相对请求中的p_a参数 */
    public static final String PARAM_REMOTE_ADDR = "remoteAddr";

    /* PARAM_PLATFORM 访问来源，相对请求中的p_1参数 */
    //public static final String PARAM_PLATFORM = "platform";

    //    public static final String PARAM_REQUESTER = "requester";
    //    public static final String PARAM_IV = "iv";
    //    public static final String PARAM_REQUEST_CHANNEL = "requestChannel";
    //    public static final String PARAM_SIGN = "sign";
    //    public static final String PARAM_TRACKING_CAMPAIGN = "trackingCampaign";
    //    public static final String PARAM_NAME = "name";

    private static ThreadLocal<String> os = new ThreadLocal<String>();
    //private static ThreadLocal<String> platform = new ThreadLocal<String>();

    private static ThreadLocal<String> uid = new ThreadLocal<String>();
    private static ThreadLocal<String> name = new ThreadLocal<String>();
    private static ThreadLocal<String> account = new ThreadLocal<String>();
    private static ThreadLocal<String> authType = new ThreadLocal<String>();
    private static ThreadLocal<String> sessionId = new ThreadLocal<String>();
    private static ThreadLocal<String> token = new ThreadLocal<String>();
    private static ThreadLocal<String> requestId = new ThreadLocal<String>();
    private static ThreadLocal<String> requester = new ThreadLocal<String>();
    private static ThreadLocal<Integer> currentHop = new ThreadLocal<Integer>();
    private static ThreadLocal<Long> timestamp = new ThreadLocal<Long>();
    private static ThreadLocal<String> remoteAddr = new ThreadLocal<String>();

    //    private static ThreadLocal<Integer> iv = new ThreadLocal<Integer>();
//    private static ThreadLocal<String> requestChannel = new ThreadLocal<String>();
//    private static ThreadLocal<String> sign = new ThreadLocal<String>();
//    private static ThreadLocal<String> trackingCampaign = new ThreadLocal<String>();
//    private static ThreadLocal<Integer> memberId = new ThreadLocal<Integer>();


    public static String getOs() {
        return os.get();
    }

    public static void setOs(String value) {
        os.set(value);
    }

    public static String getUid() {
        return uid.get();
    }

    public static void setUid(String value) {
        uid.set(value);
    }

    public static String getSessionId() {
        return sessionId.get();
    }

    public static void setSessionId(String value) {
        sessionId.set(value);
    }

    public static String getToken() {
        return token.get();
    }

    public static void setToken(String value) {
        token.set(value);
    }

    public static String getRequestId() {
        return requestId.get();
    }

    public static void setRequestId(String value) {
        requestId.set(value);
    }

    //    public static Integer getIv() {
//        return iv.get();
//    }
//
//    public static void setIv(Integer value) {
//        iv.set(value);
//    }
//    public static Integer getMemberId() {
//        return memberId.get();
//    }
//
//    public static void setMemberId(Integer value) {
//        memberId.set(value);
//    }

//    public static String getRequestChannelBase() {
//        String _requestChannel = requestChannel.get();
//        if (StringUtils.isBlank(_requestChannel)) {
//            return _requestChannel;
//        } else {
//            return StringUtils.substringBefore(_requestChannel, "_");
//        }
//    }

//    public static String getRequestChannel() {
//        return requestChannel.get();
//    }
//
//    public static void setRequestChannel(String value) {
//        requestChannel.set(value);
//    }

//    public static String getRequester() {
//        return requester.get();
//    }
//
//    public static void setRequester(String value) {
//        requester.set(value);
//    }

    //    public static String getSign() {
//        return sign.get();
//    }
//
//    public static void setSign(String value) {
//        sign.set(value);
//    }

    public static Integer getCurrentHop() {
        return currentHop.get();
    }

    public static void setCurrentHop(Integer value) {
        currentHop.set(value);
    }



    public static Long getTimestamp() {
        return timestamp.get();
    }

    public static void setTimestamp(Long value) {
        timestamp.set(value);
    }

    public static String getRemoteAddr() {
        return remoteAddr.get();
    }

    public static void setRemoteAddr(String value) {
        remoteAddr.set(value);
    }

//    public static String getTrackingCampaign() {
//        return trackingCampaign.get();
//    }
//
//    public static void setTrackingCampaign(String value) {
//        trackingCampaign.set(value);
//    }

    public static String getName() {
        return name.get();
    }

    public static void setName(String value) {
        name.set(value);
    }

    public static String getAccount() {
        return account.get();
    }

    public static void setAccount(String value) {
        account.set(value);
    }

    public static String getAuthType() {
        return authType.get();
    }

    public static void setAuthType(String value ) {
        authType.set(value);
    }

    public static void clear() {
        os.set(null);
        //platform.set(null);
//        iv.set(null);
        uid.set(null);
        sessionId.set(null);
        token.set(null);
        requestId.set(null);
//        requestChannel.set(null);
        requester.set(null);
        currentHop.set(null);
//        sign.set(null);
        timestamp.set(null);
        remoteAddr.set(null);
//        trackingCampaign.set(null);
        name.set(null);
        account.set(null);
        authType.set(null);
    }

    public static Map<String, Object> getAllParamsMap() {
        Map<String, Object> result = Maps.newLinkedHashMap();
        result.put(PARAM_OS, getOs());
        //result.put(PARAM_PLATFORM, getPlatform());
//        result.put(PARAM_IV, getIv());
        result.put(PARAM_UID, getUid());
        result.put(PARAM_SESSION_ID, getSessionId());
        result.put(PARAM_TOKEN, getToken());
        result.put(PARAM_REQUEST_ID, getRequestId());
//        result.put(PARAM_REQUEST_CHANNEL, getRequestChannel());
//        result.put(PARAM_REQUESTER, getRequester());
        result.put(PARAM_CURRENT_HOP, getCurrentHop());
//        result.put(PARAM_SIGN, getSign());
        result.put(PARAM_TIMESTAMP, getTimestamp());
        result.put(PARAM_REMOTE_ADDR, getRemoteAddr());
//        result.put(PARAM_TRACKING_CAMPAIGN, getTrackingCampaign());
        result.put(PARAM_NAME, getName());
        result.put(PARAM_ACCOUNT,getAccount());
        result.put(PARAM_AUTH_TYPE,getAuthType());
        return result;
    }

    /**
     * 平台类型
     */
    public enum OS_TYPE {
        android,ios,weixin,pc,web;
    }
}
