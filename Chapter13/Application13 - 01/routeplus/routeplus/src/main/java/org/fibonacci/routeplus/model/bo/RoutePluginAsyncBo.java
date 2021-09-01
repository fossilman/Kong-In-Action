package org.fibonacci.routeplus.model.bo;

import org.fibonacci.routeplus.model.RouteVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ：zachary
 * @description：服务
 * @date ：Created in 2020-04-20 14:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutePluginAsyncBo {

    private Integer dataId;
    private RouteVo.Plugins plugins;
    private String serviceName;
    private Boolean hasServices;
    private String pluginName;
}
