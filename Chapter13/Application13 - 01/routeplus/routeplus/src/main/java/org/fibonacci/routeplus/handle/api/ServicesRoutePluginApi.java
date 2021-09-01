package org.fibonacci.routeplus.handle.api;

import org.fibonacci.routeplus.model.bo.ServiceRoutePluginBo;

/**
 * @Description:路由插件状态管理
 * @Author: zachary
 * @Date: 2020-05-27 10:28
 */
public interface ServicesRoutePluginApi extends PluginApi{

    Boolean enabledServicePlugin(ServiceRoutePluginBo serviceRoutePluginBo);

}
