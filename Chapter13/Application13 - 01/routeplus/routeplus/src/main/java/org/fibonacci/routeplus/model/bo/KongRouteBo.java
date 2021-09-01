package org.fibonacci.routeplus.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * @author ：zachary
 * @description：路由
 * @date ：Created in 2020-04-20 14:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KongRouteBo {

    private String id;
    private String[] paths;
    private String name;
    private String upstreamId;
    private String upstreamName;
    @Builder.Default
    private Boolean strip_path = false;
    private ServiceBean service;
    private GatewayHeader headers;

    @Data
    @Builder
    public static class ServiceBean {
        private String id;
    }

    @Data
    @Builder
    public static class GatewayHeader {

        List<String> gateway;
//        @Builder.Default
        List<String> channel = Arrays.asList("other", "all");
    }
}
