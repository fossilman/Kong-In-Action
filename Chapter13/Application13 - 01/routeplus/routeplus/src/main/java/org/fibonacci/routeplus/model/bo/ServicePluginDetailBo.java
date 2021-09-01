package org.fibonacci.routeplus.model.bo;

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
public class ServicePluginDetailBo {


    /**
     * id : ce44eef5-41ed-47f6-baab-f725cecf98c7
     * name : rate-limiting
     * created_at : 1422386534
     * route : null
     * service : null
     * consumer : null
     * config : {"hour":500,"minute":20}
     * run_on : first
     * protocols : ["http","https"]
     * enabled : true
     * tags : ["user-level","low-priority"]
     */

    private String id;
    private String name;
    private Long created_at;
    private Object route;
    private Object service;
    private Object consumer;
    private ConfigBean config;
    private String run_on;
    private boolean enabled;
    private List<String> protocols;
    private List<String> tags;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConfigBean {
        /**
         * hour : 500
         * minute : 20
         */

        private int hour;
        private int minute;

    }
}
