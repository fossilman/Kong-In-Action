package org.fibonacci.framework.config;

import org.fibonacci.framework.global.AppInfo;
import org.fibonacci.framework.properties.AsyncProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

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
@Configuration
@EnableAsync
@EnableConfigurationProperties({AsyncProperties.class})
@ConditionalOnProperty(prefix = AsyncProperties.PROPERTY_PREFIX, value = "enabled")
public class AsyncConfig{

    @Autowired
    private AppInfo appInfo;

    @Autowired
    private AsyncProperties asyncProperties;

    public final static String ASYNC = "-async-";

    @Bean(name = "taskExecutor")
    public Executor frameworkAsync() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(asyncProperties.getCorePoolSize());
        executor.setMaxPoolSize(asyncProperties.getMaxPoolSize());
        executor.setKeepAliveSeconds(asyncProperties.getKeepAliveSeconds());
        executor.setQueueCapacity(asyncProperties.getQueueCapacity());
        executor.setThreadNamePrefix(appInfo.getAppName() + ASYNC);
//        executor.setThreadNamePrefix("frameworkExecutor-");
        /* CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行 */
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
