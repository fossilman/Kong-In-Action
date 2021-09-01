package org.fibonacci.routeplus.handle;

import org.fibonacci.routeplus.handle.api.PluginApi;
import org.fibonacci.routeplus.model.bo.PluginAsyncBo;
import org.fibonacci.routeplus.model.bo.RoutePluginAsyncBo;

/**
 * @Description: 顶层插件抽象类
 * @Author: zachary
 * @Date: 2020-05-26 11:55
 */
public abstract class PluginOperate {

    /**
     * 插件处理
     *
     * @param pluginAsyncBo 插件对象
     * @return 插件内部Id
     */
    public String execute(PluginAsyncBo pluginAsyncBo) {
        PluginApi api = findPluginApi();
        return api.execute(pluginAsyncBo);
    }

    /**
     * 插件处理
     *
     * @param routePluginAsyncBo 插件对象
     * @return 插件内部Id
     */
    public Integer executeRoute(RoutePluginAsyncBo routePluginAsyncBo) {
        PluginApi api = findPluginApi();
        return api.executeRoute(routePluginAsyncBo);
    }


    private PluginApi findPluginApi() {
        return this.factoryMethod();
    }

    /**
     * @return 插件api
     */
    protected abstract PluginApi factoryMethod();
}
