package org.fibonacci.routeplus.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author ：zachary
 * @description：kong状态
 * @date ：Created in 2020-04-20 14:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KongStatusVo {

    /**
     * database : {"reachable":true}
     * memory : {"workers_lua_vms":[{"http_allocated_gc":"0.02 MiB","pid":18477},{"http_allocated_gc":"0.02 MiB","pid":18478}],"lua_shared_dicts":{"kong":{"allocated_slabs":"0.04 MiB","capacity":"5.00 MiB"},"kong_db_cache":{"allocated_slabs":"0.80 MiB","capacity":"128.00 MiB"}}}
     * server : {"total_requests":3,"connections_active":1,"connections_accepted":1,"connections_handled":1,"connections_reading":0,"connections_writing":1,"connections_waiting":0}
     */

    private DatabaseBean database;
    //private MemoryBean memory;
    //private ServerBean server;
    private Integer node;

    @Data
    public static class DatabaseBean {
        /**
         * reachable : true
         */

        private boolean reachable;


    }

    @Data
    public static class MemoryBean {
        /**
         * workers_lua_vms : [{"http_allocated_gc":"0.02 MiB","pid":18477},{"http_allocated_gc":"0.02 MiB","pid":18478}]
         * lua_shared_dicts : {"kong":{"allocated_slabs":"0.04 MiB","capacity":"5.00 MiB"},"kong_db_cache":{"allocated_slabs":"0.80 MiB","capacity":"128.00 MiB"}}
         */

        private LuaSharedDictsBean lua_shared_dicts;
        private List<WorkersLuaVmsBean> workers_lua_vms;


        @Data
        public static class LuaSharedDictsBean {
            /**
             * kong : {"allocated_slabs":"0.04 MiB","capacity":"5.00 MiB"}
             * kong_db_cache : {"allocated_slabs":"0.80 MiB","capacity":"128.00 MiB"}
             */

            private KongBean kong;
            private KongDbCacheBean kong_db_cache;


            @Data
            public static class KongBean {
                /**
                 * allocated_slabs : 0.04 MiB
                 * capacity : 5.00 MiB
                 */

                private String allocated_slabs;
                private String capacity;


            }

            @Data
            public static class KongDbCacheBean {
                /**
                 * allocated_slabs : 0.80 MiB
                 * capacity : 128.00 MiB
                 */

                private String allocated_slabs;
                private String capacity;


            }
        }

        @Data
        public static class WorkersLuaVmsBean {
            /**
             * http_allocated_gc : 0.02 MiB
             * pid : 18477
             */

            private String http_allocated_gc;
            private int pid;


        }
    }

    @Data
    public static class ServerBean {
        /**
         * total_requests : 3
         * connections_active : 1
         * connections_accepted : 1
         * connections_handled : 1
         * connections_reading : 0
         * connections_writing : 1
         * connections_waiting : 0
         */

        private int total_requests;
        private int connections_active;
        private int connections_accepted;
        private int connections_handled;
        private int connections_reading;
        private int connections_writing;
        private int connections_waiting;


    }
}
