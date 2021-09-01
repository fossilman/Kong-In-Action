package org.fibonacci.routeplus.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ：zachary
 * @description：路由对象
 * @date ：Created in 2019-09-17 10:57
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PluginVo {

    //插件id
    private String id;
    private Integer serviceId;
    private Integer routeId;
}
