package org.fibonacci.framework.util;

import org.fibonacci.framework.threadlocal.ParameterThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

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
public class RequestIdUtil {

    /* 内部调用或者外部调用没有带r_i的补全 */
    public static final String INTERNAL_INVOKE_SUFFIX = ".W";
    /* 移动端调用 */
    public static final String MOBILE_INVOKE_SUFFIX = ".M";
    /* job调用 */
    public static final String JOB_INVOKE_SUFFIX = ".J";

    private static final int INIT_HOP = 1;

    public static String buildNextRequestId() {

        String requestId = ParameterThreadLocal.getRequestId();

        if (StringUtils.isEmpty(requestId)) {
            String suffix = INTERNAL_INVOKE_SUFFIX;
            requestId = UUID.randomUUID().toString() + suffix;
            ParameterThreadLocal.setRequestId(requestId);

        }

        Integer currentHop = ParameterThreadLocal.getCurrentHop();
        if (currentHop == null) {
            currentHop = INIT_HOP;
        } else {
            currentHop++;
        }
        if (currentHop >= Integer.MAX_VALUE) {
            // 防止长线程hop加到溢出
            currentHop = INIT_HOP;
        }
        ParameterThreadLocal.setCurrentHop(currentHop);

        return requestId + "." + currentHop;
    }

    public static void initRequestId() {

        String requestId = UUID.randomUUID().toString() + INTERNAL_INVOKE_SUFFIX;
        ParameterThreadLocal.setRequestId(requestId);
    }

    /**
     * 判断手机还是PC
     *
     * @param agent
     * @return
     *//*
    public static String isMobileDevice(String agent) {
        String flag = ParameterThreadLocal.OS_TYPE.PC.name();
        if (StringUtils.isBlank(agent)) {
            return flag;
        }

        *//**
         * android : 所有android设备
         * iPhone iPod : 苹果
         * windows phone:Nokia等windows系统的手机
         * MQQBrowser:qq浏览器
         *//*
        agent = agent.toLowerCase();
        String[] deviceArray = new String[]{"Android", "iPhone", "iPod", "Windows Phone", "MQQBrowser"};
        //排除 Windows 桌面系统
        if (!agent.contains("Windows NT") || (agent.contains("Windows NT") && agent.contains("compatible; MSIE 9.0;"))) {
            //排除 苹果桌面系统
            if (!agent.contains("Windows NT") && !agent.contains("Macintosh")) {
                for (String device : deviceArray) {
                    if (agent.contains(device.toLowerCase())) {
                        flag = ParameterThreadLocal.OS_TYPE.MOBILE.name();
                        break;
                    }
                }
            }
        }

        return flag;
    }*/

    /**
     * ServletRequestAttributes
     *
     * @return
     */
    public static ServletRequestAttributes servletRequestAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

    /**
     * 获取request
     *
     * @return
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes servletRequestAttributes = servletRequestAttributes();
        return servletRequestAttributes == null ? null : servletRequestAttributes.getRequest();
    }

    /**
     * response
     *
     * @return
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes servletRequestAttributes = servletRequestAttributes();
        return servletRequestAttributes == null ? null : servletRequestAttributes.getResponse();
    }

    /**
     * 获取UserAgent
     *
     * @return
     */
    public static String getUserAgent(HttpServletRequest request) {

        if (request == null) {
            request = getRequest();
        }
        return request == null ? "" : request.getHeader("user-agent");
    }


    /**
     * 判断手机还是PC根据header里的device字段
     * device为null或0时 请求为移动端请求
     * device为其他值时 请求为pc端请求
     *
     * @param request
     * @return
     */
    public static Boolean isMobileDevice(HttpServletRequest request) {

        if (StringUtils.isBlank(request.getHeader("device")) || request.getHeader("device").equals("0")) {
            return true;
        }

        return false;
    }

}
