package org.fibonacci.routeplus.handle.api;

import org.fibonacci.routeplus.model.RouteVo;
import org.fibonacci.routeplus.model.bo.RateLimitingBo;

/**
 * @Description:插件类型api
 * @Author: zachary
 * @Date: 2020-05-26 12:05
 */
public interface PluginTypeApi {

    /**
     * 插件对象映射
     *
     * @param plugin
     * @return
     */
    RateLimitingBo mapping(RouteVo.Plugins plugin);

}
