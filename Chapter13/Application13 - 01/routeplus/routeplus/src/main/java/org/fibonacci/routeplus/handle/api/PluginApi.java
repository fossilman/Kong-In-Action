package org.fibonacci.routeplus.handle.api;

import org.fibonacci.routeplus.model.bo.PluginAsyncBo;
import org.fibonacci.routeplus.model.bo.RoutePluginAsyncBo;

/**
 * @Description:插件api
 * @Author: zachary
 * @Date: 2020-05-26 12:05
 */
public interface PluginApi {

    /**
     * 插件业务逻辑处理
     *
     * @param pluginAsyncBo
     */
    String execute(PluginAsyncBo pluginAsyncBo);


    /**
     * 自定义插件业务逻辑处理
     *
     * @param routePluginAsyncBo
     */
    Integer executeRoute(RoutePluginAsyncBo routePluginAsyncBo);
}
