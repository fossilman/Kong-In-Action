package org.fibonacci.routeplus.handle.operate;

import org.fibonacci.routeplus.handle.ServicesRouteOperate;
import org.fibonacci.routeplus.handle.api.PluginApi;
import org.fibonacci.routeplus.handle.api.ServicesRoutePluginApi;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description: 限流插件
 * @Author: zachary
 * @Date: 2020-05-26 11:55
 */
@Component
public class RateLimitingPluginOperate extends ServicesRouteOperate {

    @Resource
    private RateLimitingPlugin rateLimitingPlugin;

    @Override
    protected PluginApi factoryMethod() {
        return rateLimitingPlugin;
    }

    @Override
    protected ServicesRoutePluginApi abstractFactoryMethod() {
        return rateLimitingPlugin;
    }
}
