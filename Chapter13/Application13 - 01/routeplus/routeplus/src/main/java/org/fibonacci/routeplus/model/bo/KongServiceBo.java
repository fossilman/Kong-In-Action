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
public class KongServiceBo {

    private String id;
    private String name;
    private String host;
    private Integer port;
    private String upstreamId;
    private String upstreamName;
}
