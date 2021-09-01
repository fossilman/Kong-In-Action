package org.fibonacci.routeplus.model.bo;

import org.fibonacci.routeplus.domain.ServicesRoutePlugin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author ：zachary
 * @description：服务
 * @date ：Created in 2020-04-20 14:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRoutePluginBo {

    private Integer id;
    private List<ServicesRoutePlugin> servicesRoutePlugins;
    private String kongRoutesName;

}
