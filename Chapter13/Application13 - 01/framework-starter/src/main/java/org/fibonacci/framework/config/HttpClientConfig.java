package org.fibonacci.framework.config;

import org.fibonacci.framework.httpclient.HttpClientTemplate;
import org.fibonacci.framework.properties.HttpClientProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
@EnableConfigurationProperties({HttpClientProperties.class})
public class HttpClientConfig {

    @Autowired
    private HttpClientProperties httpClientProperties;

    @Bean
    public HttpClientTemplate httpClient() {

        return new HttpClientTemplate(httpClientProperties.getMaxTotal(), httpClientProperties.getDefaultMaxPerRoute(),
                httpClientProperties.getConnectionTimeout(), httpClientProperties.getSocketTimeout());
    }
}
