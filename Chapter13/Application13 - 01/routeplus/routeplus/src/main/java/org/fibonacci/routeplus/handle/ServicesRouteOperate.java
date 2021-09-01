package org.fibonacci.routeplus.handle;

import org.fibonacci.routeplus.handle.api.ServicesRoutePluginApi;
import org.fibonacci.routeplus.model.bo.ServiceRoutePluginBo;

/**
 * @Description: 顶层路由插件抽象类
 * @Author: zachary
 * @Date: 2020-05-27 10:32
 */
public abstract class ServicesRouteOperate extends PluginOperate {


    /**
     * 路由插件处理
     *
     * @param serviceRoutePluginBo 插件对象
     * @return 插件内部Id
     */
    public Boolean enabledServicePlugin(ServiceRoutePluginBo serviceRoutePluginBo) {
        ServicesRoutePluginApi api = findServicesRoutePluginApi();
        return api.enabledServicePlugin(serviceRoutePluginBo);
    }


    private ServicesRoutePluginApi findServicesRoutePluginApi() {
        return this.abstractFactoryMethod();
    }

    /**
     * @return 插件api
     */
    protected abstract ServicesRoutePluginApi abstractFactoryMethod();
}
