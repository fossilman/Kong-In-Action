package org.fibonacci.routeplus.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ：zachary
 * @description：服务
 * @date ：Created in 2020-04-20 14:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LimitingPluginBo {

    /**
     * "service_" + upstream.getName()
     * "route_" + upstream.getName() + "_web"
     * "/web/web_" + upstream.getName()
     */
    private String pluginId;
    private String name = "rate-limiting";
    private String upstreamName;
    private String routeType;
    private Map config;
    private String serviceName;
    private Boolean enabled;
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConfigAttr {
        private int hour;
        private int second;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LimitingPluginUpdateBo {
        private Map config = new HashMap();
        private Boolean enabled = true;
    }
}
