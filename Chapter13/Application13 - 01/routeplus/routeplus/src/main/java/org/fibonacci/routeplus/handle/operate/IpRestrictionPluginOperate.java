package org.fibonacci.routeplus.handle.operate;

import org.fibonacci.routeplus.handle.ServicesRouteOperate;
import org.fibonacci.routeplus.handle.api.PluginApi;
import org.fibonacci.routeplus.handle.api.ServicesRoutePluginApi;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description: 黑白名单
 * @Author: zachary
 * @Date: 2020-05-26 11:55
 */
@Component
public class IpRestrictionPluginOperate extends ServicesRouteOperate {

    @Resource
    private IpRestrictionPlugin ipRestrictionPlugin;

    @Override
    protected PluginApi factoryMethod() {
        return ipRestrictionPlugin;
    }

    @Override
    protected ServicesRoutePluginApi abstractFactoryMethod() {
        return ipRestrictionPlugin;
    }
}
