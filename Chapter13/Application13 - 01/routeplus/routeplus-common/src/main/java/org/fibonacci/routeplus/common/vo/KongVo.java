package org.fibonacci.routeplus.common.vo;

import org.fibonacci.routeplus.common.bo.TargetBo;
import lombok.Data;

import java.util.List;

/**
 * @author ：zachary
 * @description：返回结果
 * @date ：Created in 2019-09-17 11:19
 */
@Data
public class KongVo {


    private List<TargetBo>  data;


    @Data
    public static class Services{

        /**
         * next : null
         * data : [{"host":"point","created_at":1570507885,"connect_timeout":60000,"id":"1142ac60-eefb-4e26-8f90-d2cf94c601ea","protocol":"http","name":"service_point","read_timeout":60000,"port":80,"path":null,"updated_at":1570507885,"retries":5,"write_timeout":60000,"tags":null,"client_certificate":null},{"host":"publish","created_at":1569048398,"connect_timeout":60000,"id":"7d114c42-8f12-46e3-8e29-d69190de53be","protocol":"http","name":"service_publish1111","read_timeout":60000,"port":80,"path":null,"updated_at":1569048398,"retries":5,"write_timeout":60000,"tags":null,"client_certificate":null},{"host":"publish10","created_at":1569049851,"connect_timeout":60000,"id":"f04c5d23-5aca-4184-a4bf-29265e6078c3","protocol":"http","name":"service_publish10","read_timeout":60000,"port":80,"path":null,"updated_at":1569049851,"retries":5,"write_timeout":60000,"tags":null,"client_certificate":null}]
         */

        private Object next;
        private List<DataBean> data;

        @Data
        public static class DataBean {
            /**
             * host : point
             * created_at : 1570507885
             * connect_timeout : 60000
             * id : 1142ac60-eefb-4e26-8f90-d2cf94c601ea
             * protocol : http
             * name : service_point
             * read_timeout : 60000
             * port : 80
             * path : null
             * updated_at : 1570507885
             * retries : 5
             * write_timeout : 60000
             * tags : null
             * client_certificate : null
             */

            private String host;
            private int created_at;
            private int connect_timeout;
            private String id;
            private String protocol;
            private String name;
            private int read_timeout;
            private int port;
            private Object path;
            private int updated_at;
            private int retries;
            private int write_timeout;
            private Object tags;
            private Object client_certificate;

        }
    }

    @Data
    public static class ServicesDetail{

        /**
         * host : point
         * created_at : 1571300129
         * connect_timeout : 60000
         * id : 9dc013cf-57fd-4065-ae20-1251227a9ed3
         * protocol : http
         * name : service_point
         * read_timeout : 60000
         * port : 80
         * path : null
         * updated_at : 1571300129
         * retries : 5
         * write_timeout : 60000
         * tags : null
         */

        private String host;
        private int created_at;
        private int connect_timeout;
        private String id;
        private String protocol;
        private String name;
        private int read_timeout;
        private int port;
        private Object path;
        private int updated_at;
        private int retries;
        private int write_timeout;
        private Object tags;

    }

    @Data
    public static class Routes{

        /**
         * next : null
         * data : [{"id":"174ad8af-b6ba-4177-af9c-1def337e05f7","tags":null,"paths":["/web/web_studycenter"],"destinations":null,"protocols":["http","https"],"created_at":1567997130,"snis":null,"hosts":null,"name":"route_studycenter_web","preserve_host":false,"regex_priority":0,"strip_path":true,"sources":null,"updated_at":1567997130,"https_redirect_status_code":426,"service":{"id":"428ea41b-faf8-45e6-8dbd-7f81a8dcc73b"},"methods":null},{"id":"547ada84-88ed-462e-b6fd-42ef5c318063","tags":null,"paths":["/web/web_point"],"destinations":null,"protocols":["http","https"],"created_at":1567997138,"snis":null,"hosts":null,"name":"route_point_web","preserve_host":false,"regex_priority":0,"strip_path":true,"sources":null,"updated_at":1567997138,"https_redirect_status_code":426,"service":{"id":"ebd6fc66-081b-47fd-ba5a-3ebcb6d824da"},"methods":null}]
         */

        private Object next;
        private List<DataBean> data;

        @Data
        public static class DataBean {
            /**
             * id : 174ad8af-b6ba-4177-af9c-1def337e05f7
             * tags : null
             * paths : ["/web/web_studycenter"]
             * destinations : null
             * protocols : ["http","https"]
             * created_at : 1567997130
             * snis : null
             * hosts : null
             * name : route_studycenter_web
             * preserve_host : false
             * regex_priority : 0
             * strip_path : true
             * sources : null
             * updated_at : 1567997130
             * https_redirect_status_code : 426
             * service : {"id":"428ea41b-faf8-45e6-8dbd-7f81a8dcc73b"}
             * methods : null
             */

            private String id;
            private Object tags;
            private Object destinations;
            private int created_at;
            private Object snis;
            private Object hosts;
            private String name;
            private boolean preserve_host;
            private int regex_priority;
            private boolean strip_path;
            private Object sources;
            private int updated_at;
            private int https_redirect_status_code;
            private ServiceBean service;
            private Object methods;
            private List<String> paths;
            private List<String> protocols;

            @Data
            public static class ServiceBean {
                /**
                 * id : 428ea41b-faf8-45e6-8dbd-7f81a8dcc73b
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
    }


    @Data
    public static class RoutesDetail{

        /**
         * next : null
         * data : [{"id":"e745f114-6eaf-477e-bc38-29545a6d6730","tags":null,"paths":["/web/web_point"],"destinations":null,"protocols":["http","https"],"created_at":1571300129,"snis":null,"hosts":null,"name":"route_point_web","preserve_host":false,"regex_priority":0,"strip_path":true,"sources":null,"updated_at":1571300129,"https_redirect_status_code":426,"service":{"id":"9dc013cf-57fd-4065-ae20-1251227a9ed3"},"methods":null}]
         */

        private Object next;
        private List<DataBean> data;

        public Object getNext() {
            return next;
        }

        public void setNext(Object next) {
            this.next = next;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        @Data
        public static class DataBean {
            /**
             * id : e745f114-6eaf-477e-bc38-29545a6d6730
             * tags : null
             * paths : ["/web/web_point"]
             * destinations : null
             * protocols : ["http","https"]
             * created_at : 1571300129
             * snis : null
             * hosts : null
             * name : route_point_web
             * preserve_host : false
             * regex_priority : 0
             * strip_path : true
             * sources : null
             * updated_at : 1571300129
             * https_redirect_status_code : 426
             * service : {"id":"9dc013cf-57fd-4065-ae20-1251227a9ed3"}
             * methods : null
             */

            private String id;
            private Object tags;
            private Object destinations;
            private int created_at;
            private Object snis;
            private Object hosts;
            private String name;
            private boolean preserve_host;
            private int regex_priority;
            private boolean strip_path;
            private Object sources;
            private int updated_at;
            private int https_redirect_status_code;
            private ServiceBean service;
            private Object methods;
            private List<String> paths;
            private List<String> protocols;

            @Data
            public static class ServiceBean {
                /**
                 * id : 9dc013cf-57fd-4065-ae20-1251227a9ed3
                 */

                private String id;

            }
        }
    }



}

