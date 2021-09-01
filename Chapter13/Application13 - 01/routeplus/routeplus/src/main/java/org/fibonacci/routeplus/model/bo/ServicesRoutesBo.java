package org.fibonacci.routeplus.model.bo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author ：zachary
 * @description：
 * @date ：Created in 2020-05-19 09:47
 */
@Data
@Builder
public class ServicesRoutesBo {

    /**
     * 主键
     */
    @JsonProperty("routeId")
    private Integer id;

    /**
     * 路由类型(pc/web/openapi)
     */
    private String routeType;
}
