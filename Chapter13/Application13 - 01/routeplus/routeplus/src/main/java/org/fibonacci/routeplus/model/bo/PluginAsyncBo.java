package org.fibonacci.routeplus.model.bo;

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
public class PluginAsyncBo {

    private Integer id;
    private String pluginId;
    private String pluginName;
    private String upstreamName;
    private String name;
    private String routeType;
    private Boolean enabled;
    private String gName;
    private RateLimitingBo rateLimitingBo;

}
