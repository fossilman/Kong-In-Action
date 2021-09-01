package org.fibonacci.routeplus.common.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author ：zachary
 * @description： 网关
 * @date ：Created in 2020-04-20 13:56
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpstreamBo {


    /**
     * id : 58c8ccbb-eafb-4566-991f-2ed4f678fa70
     * created_at : 1422386534
     * name : my-upstream
     * algorithm : round-robin
     * hash_on : none
     * hash_fallback : none
     * hash_on_cookie_path : /
     * slots : 10000
     * healthchecks : {"active":{"https_verify_certificate":true,"unhealthy":{"http_statuses":[429,404,500,501,502,503,504,505],"tcp_failures":0,"timeouts":0,"http_failures":0,"interval":0},"http_path":"/","timeout":1,"healthy":{"http_statuses":[200,302],"interval":0,"successes":0},"https_sni":"example.com","concurrency":10,"type":"http"},"passive":{"unhealthy":{"http_failures":0,"http_statuses":[429,500,503],"tcp_failures":0,"timeouts":0},"type":"http","healthy":{"successes":0,"http_statuses":[200,201,202,203,204,205,206,207,208,226,300,301,302,303,304,305,306,307,308]}}}
     * tags : ["user-level","low-priority"]
     */

    private String id;
    private String name;
    //private String algorithm;
    //private String hash_on;
    //private String hash_fallback;
    //private String hash_on_cookie_path;
    //private int slots;
    private HealthchecksBean healthchecks;
    //private List<String> tags;


    @Data
    @Builder
    public static class HealthchecksBean {
        /**
         * active : {"https_verify_certificate":true,"unhealthy":{"http_statuses":[429,404,500,501,502,503,504,505],"tcp_failures":0,"timeouts":0,"http_failures":0,"interval":0},"http_path":"/","timeout":1,"healthy":{"http_statuses":[200,302],"interval":0,"successes":0},"https_sni":"example.com","concurrency":10,"type":"http"}
         * passive : {"unhealthy":{"http_failures":0,"http_statuses":[429,500,503],"tcp_failures":0,"timeouts":0},"type":"http","healthy":{"successes":0,"http_statuses":[200,201,202,203,204,205,206,207,208,226,300,301,302,303,304,305,306,307,308]}}
         */

        private ActiveBean active;
        //private PassiveBean passive;

        @Data
        @Builder
        public static class ActiveBean {
            /**
             * https_verify_certificate : true
             * unhealthy : {"http_statuses":[429,404,500,501,502,503,504,505],"tcp_failures":0,"timeouts":0,"http_failures":0,"interval":0}
             * http_path : /
             * timeout : 1
             * healthy : {"http_statuses":[200,302],"interval":0,"successes":0}
             * https_sni : example.com
             * concurrency : 10
             * type : http
             */
            //private boolean https_verify_certificate;
            private String http_path;
            private Integer timeout;
            private Integer concurrency;
            private HealthyBean healthy;
            private UnhealthyBean unhealthy;
            //private String https_sni;

            //private String type;

            @Data
            @Builder
            public static class UnhealthyBean {
                /**
                 * http_statuses : [429,404,500,501,502,503,504,505]
                 * tcp_failures : 0
                 * timeouts : 0
                 * http_failures : 0
                 * interval : 0
                 */
                //private int tcp_failures;
                private Integer timeouts;
                //private int http_failures;
                private Integer interval;
                //private List<Integer> http_statuses;


            }

            @Data
            @Builder
            public static class HealthyBean {
                /**
                 * http_statuses : [200,302]
                 * interval : 0
                 * successes : 0
                 */
                private Integer interval;
                private Integer successes;
                //private List<Integer> http_statuses;

            }
        }

        @Data
        public static class PassiveBean {
            /**
             * unhealthy : {"http_failures":0,"http_statuses":[429,500,503],"tcp_failures":0,"timeouts":0}
             * type : http
             * healthy : {"successes":0,"http_statuses":[200,201,202,203,204,205,206,207,208,226,300,301,302,303,304,305,306,307,308]}
             */

            private UnhealthyBeanX unhealthy;
            private String type;
            private HealthyBeanX healthy;


            @Data
            public static class UnhealthyBeanX {
                /**
                 * http_failures : 0
                 * http_statuses : [429,500,503]
                 * tcp_failures : 0
                 * timeouts : 0
                 */

                private int http_failures;
                private int tcp_failures;
                private int timeouts;
                private List<Integer> http_statuses;

            }

            @Data
            public static class HealthyBeanX {
                /**
                 * successes : 0
                 * http_statuses : [200,201,202,203,204,205,206,207,208,226,300,301,302,303,304,305,306,307,308]
                 */

                private int successes;
                private List<Integer> http_statuses;


            }
        }
    }
}
