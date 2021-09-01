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
public class LimitingPluginDetailBo {

    /**
     * created_at : 1589342741
     * config : {"minute":null,"policy":"cluster","month":null,"redis_timeout":2000,"limit_by":"consumer","hide_client_headers":false,"second":5,"day":null,"redis_password":null,"year":null,"redis_database":0,"hour":500,"redis_port":6379,"redis_host":null,"fault_tolerant":true}
     * id : 6fcd278f-2287-4c3c-8451-a9e83044ba70
     * service : null
     * name : rate-limiting
     * protocols : ["http","https"]
     * enabled : true
     * run_on : first
     * consumer : null
     * route : {"id":"6041cfc8-6f85-472c-a3c3-f9534fb349f7"}
     * tags : null
     */

    private Long created_at;
    private ConfigBean config;
    private String id;
    private Object service;
    private String name;
    private boolean enabled;
    private String run_on;
    private Object consumer;
    //private RouteBean route;
    private Object tags;
    private List<String> protocols;



    @Data
    public static class ConfigBean {
        /**
         * minute : null
         * policy : cluster
         * month : null
         * redis_timeout : 2000
         * limit_by : consumer
         * hide_client_headers : false
         * second : 5
         * day : null
         * redis_password : null
         * year : null
         * redis_database : 0
         * hour : 500
         * redis_port : 6379
         * redis_host : null
         * fault_tolerant : true
         */

        private Object minute;
        private String policy;
        private Object month;
        private int redis_timeout;
        private String limit_by;
        private boolean hide_client_headers;
        private int second;
        private Object day;
        private Object redis_password;
        private Object year;
        private int redis_database;
        private int hour;
        private int redis_port;
        private Object redis_host;
        private boolean fault_tolerant;


    }

    @Data
    public static class RouteBean {
        /**
         * id : 6041cfc8-6f85-472c-a3c3-f9534fb349f7
         */

        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
