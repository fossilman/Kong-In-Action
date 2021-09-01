package org.fibonacci.routeplus.common.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ：zachary
 * @description：
 * @date ：Created in 2020-04-20 14:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetBo {

    private String id;
    private String target;
    private Long weight;
    private String upstreamId;
    private String upstreamName;
}
