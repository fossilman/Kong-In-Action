package org.fibonacci.framework.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySources;
import org.springframework.util.Assert;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.SystemPropertyUtils;

import java.util.NoSuchElementException;

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
@Slf4j
public class ConfigUtil {

    private static PropertySources getSources(Environment environment) {
        Assert.notNull(environment, "Environment must not be null");
        Assert.isInstanceOf(ConfigurableEnvironment.class, environment,
                "Environment must be a ConfigurableEnvironment");
        return ((ConfigurableEnvironment) environment).getPropertySources();
    }

    @SuppressWarnings("unchecked")
    public static <T> T getConfig(Environment environment, String prefix, Class<T> claz) {
        try {
            PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper(SystemPropertyUtils.PLACEHOLDER_PREFIX,
                    SystemPropertyUtils.PLACEHOLDER_SUFFIX,
                    SystemPropertyUtils.VALUE_SEPARATOR, true);
            PropertySources sources = getSources(environment);

            PropertySourcesPlaceholdersResolver placeholdersResolver = new PropertySourcesPlaceholdersResolver(sources, helper);
            Binder binder = new Binder(ConfigurationPropertySources.get(environment), placeholdersResolver);
            BindResult<T> binderResult = binder.bind(prefix, claz);
            return binderResult.get();
        } catch (NoSuchElementException e) {
            log.info("没有发现配置，prefix={}", prefix);
            return null;
        } catch (Exception e) {
            log.error("解析配置参数失败, prefix={}", prefix, e);
            throw new RuntimeException(e);
        }
    }
}
