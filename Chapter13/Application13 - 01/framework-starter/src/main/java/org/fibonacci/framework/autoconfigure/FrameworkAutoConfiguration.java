package org.fibonacci.framework.autoconfigure;

import org.fibonacci.framework.config.AsyncConfig;
import org.fibonacci.framework.config.ControllerConfig;
import org.fibonacci.framework.config.HttpClientConfig;
import org.fibonacci.framework.global.AppInfo;
import org.fibonacci.framework.httpclient.InitParameters;
import org.fibonacci.framework.util.EnvUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.Assert;

import java.util.Arrays;

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
@ServletComponentScan(basePackages = "org.fibonacci.framework")
@Import({
        ControllerConfig.class,
        HttpClientConfig.class,
        AsyncConfig.class,
        BuildInfo.class})
@Slf4j
public class FrameworkAutoConfiguration {

    @Value("${spring.application.name:unkown}")
    private String appName;
    @Value("${server.address:localhost}")
    private String serverIp;
    @Value("${server.port:8080}")
    private Integer serverPort;
    @Value("${fibonacci.host:localhost}")
    private String hostIp;
    @Value("${fibonacci.host.name:localhost}")
    private String hostName;
    @Value("${spring.profiles.active}")
    private String env;

    @Bean
    public AppInfo appInfo() {
        Assert.hasText(appName, "spring.application.name can not be empty");
        // TODO 之后修改为注入的方式引入
        InitParameters.setContextPath(appName);
        AppInfo environment = new AppInfo();
        environment.setAppName(appName);
        environment.setClusterName(EnvUtil.getClusterName());
        environment.setEnv(env);
        environment.setHostIp(hostIp);
        environment.setHostName(hostName);
        environment.setServerIp(serverIp);
        environment.setServerPort(serverPort);
        return environment;
    }

}
