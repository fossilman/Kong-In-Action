package org.fibonacci.routeplus.model;

import org.fibonacci.routeplus.domain.Plugin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：zachary
 * @description：路由对象
 * @date ：Created in 2019-09-17 10:57
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteVo {

    private Integer id;
    private List<Plugin> plugins = new ArrayList<>();
    private Applications applications = new Applications();
    private List<Gateways> gateways = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Applications {
        private List<Plugins> plugins = new ArrayList<>();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Plugins {
        private Integer id;
        private Integer pluginId;
        private String pluginName;
        private Boolean enabled;
        private PluginAttr pluginAttr = new PluginAttr();
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PluginAttr {
        private String id;
        /**
         * 限流插件
         */
        private Integer quantity;
        private String period;
        private Boolean enabled;

        /**
         * 黑白名单
         */
        private String[] whitelist;
        private String[] blacklist;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Gateways {
        private String type;
        private List<Plugins> plugins = new ArrayList<>();
    }
}
