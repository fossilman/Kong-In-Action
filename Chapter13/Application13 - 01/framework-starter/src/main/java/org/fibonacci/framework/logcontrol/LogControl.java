package org.fibonacci.framework.logcontrol;

import org.fibonacci.framework.threadlocal.ParameterThreadLocal;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

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
public class LogControl {

    private static Map<LogType, LogSampler> samplerMap = new ConcurrentHashMap<>();


    /**
     * 日志类型
     * HttpIn: Http请求入口日志
     * HttpOut: Http请求出口日志
     * MsgIn: RabbitMQ消息消费者日志
     * MsgOut: RabbitMQ消息生产者日志
     * SlowSql: 慢SQL语句日志
     */
    public enum LogType{
        HttpIn, HttpOut, MsgIn, MsgOut, SlowSql, Client;
    }

    /**
     * default log control
     */
    private static final HttpLogControlConfig DEFAULT_LOG_CONTROL = new HttpLogControlConfig(){

        @Override
        public Class<? extends Annotation> annotationType() {
            return HttpLogControlConfig.class;
        }

        @Override
        public HttpLogItem[] excludesFromAll() {
            return new HttpLogItem[]{HttpLogItem.requestHeaders};
        }

        @Override
        public HttpLogItem[] justIncludes() {
            return HttpLogItem.values();
        }

        @Override
        public int sampleRate() {
            return 100;
        }
    };


    static{
        samplerMap.put(LogType.HttpIn, new HttpInLogSampler());
        samplerMap.put(LogType.HttpOut, new HttpOutLogSampler());
    }

    /**
     * 本次采样是否命中
     *
     * @param logType
     * @param logControl
     * @return true:命中，no:没有命中
     */
    public static boolean successSampled(LogType logType, HttpLogControlConfig logControl) {

        // user does not explicitly specify log control
        if (logControl == null) {
            logControl = DEFAULT_LOG_CONTROL;
        }

        int sampleRate = logControl.sampleRate();

        // adjust to valid range
        if(sampleRate<0){
            sampleRate = 0;
        }else if(sampleRate>100){
            sampleRate = 100;
        }

        // user hope no sample, so no log
        if(sampleRate == 0){
            return false;
        }

        // user hope to sample with rate
        return samplerMap.get(logType).canPrint(ParameterThreadLocal.getRequestId(), sampleRate);
    }


    /**
     * @param logControlConfig log control config
     * @param logMapAll log map user prepared to print out
     */
    public static void filterByLogControl(HttpLogControlConfig logControlConfig, Map<HttpLogItem, Object> logMapAll) {

        // user does not explicitly specify log control
        if (logControlConfig == null) {
            logControlConfig = DEFAULT_LOG_CONTROL;
        }

        HttpLogItem[] excludesFromAll = logControlConfig.excludesFromAll();
        if(excludesFromAll.length > 0){

            Arrays.stream(excludesFromAll).forEach(logMapAll::remove);

            // when excludes specified, justIncludes will be ignored
            return;
        }


        HttpLogItem[] justIncludes = logControlConfig.justIncludes();

        if(justIncludes.length > 0){

            Map<HttpLogItem,Object> tmpMap = new LinkedHashMap<>();
            Set<HttpLogItem> justIncludesSet = Arrays.stream(justIncludes).collect(Collectors.toSet());


            justIncludesSet.forEach(e->{
                if(logMapAll.containsKey(e)){
                    tmpMap.put(e, logMapAll.get(e));
                }
            });

            logMapAll.clear();
            logMapAll.putAll(tmpMap);
        }
    }

    /**
     * sampler interface
     *
     * @param <T>
     * @param <K>
     */
    public interface LogSampler<T, K>{

        boolean canPrint(T logSamplerSeed, K logSamplerRateLimit);
    }

    /**
     * log sampler for http in log
     *
     */
    static class HttpInLogSampler implements LogSampler<String,Integer> {

        private LongAdder printed = new LongAdder();
        private LongAdder total = new LongAdder();

        private double currentRate() {
            return printed.doubleValue() == 0.0 ? 0 : (printed.doubleValue() + 1.0) / (total.doubleValue() + 1.0) * 100;
        }

        @Override
        public boolean canPrint(String logSamplerSeed, Integer logSamplerRateLimit) {
            if (total.longValue() >= Long.MAX_VALUE) {
                synchronized (HttpInLogSampler.class) {
                    total.reset();
                    printed.reset();
                }
            }
            total.increment();
            if (currentRate() <= logSamplerRateLimit) {
                printed.increment();
                return true;
            }
            return false;
        }
    }

    /**
     * log sampler for http out log
     *
     */
    private static class HttpOutLogSampler extends HttpInLogSampler{

    }
}
