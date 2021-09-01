package org.fibonacci.routeplus.constants;

import org.fibonacci.routeplus.handle.api.PluginTypeApi;
import org.fibonacci.routeplus.model.RouteVo;
import org.fibonacci.routeplus.model.bo.RateLimitingBo;

/**
 * @Description:
 * @Author: zachary
 * @Date: 2020-05-26 14:30
 */
public enum PluginEnum implements PluginTypeApi {

    /**
     * 限流
     */
    rateLimiting() {
        @Override
        public RateLimitingBo mapping(RouteVo.Plugins plugins) {
            RateLimitingBo rateLimitingBo = new RateLimitingBo();
            if (plugins == null || plugins.getPluginAttr() == null) {
                return rateLimitingBo;
            }

            RouteVo.PluginAttr plugin = plugins.getPluginAttr();
            if (plugin.getQuantity() != null) {
                rateLimitingBo.setQuantity(plugin.getQuantity());
            }
            if (plugin.getPeriod() != null) {
                rateLimitingBo.setPeriod(plugin.getPeriod());
            }
            rateLimitingBo.setEnabled(plugin.getEnabled() == null ? true : plugin.getEnabled());
            return rateLimitingBo;
        }
    },

    /**
     * 黑白名单
     */
    ipRestriction() {
        @Override
        public RateLimitingBo mapping(RouteVo.Plugins plugins) {
            RateLimitingBo rateLimitingBo = new RateLimitingBo();
            if (plugins == null || plugins.getPluginAttr() == null) {
                return rateLimitingBo;
            }

            RouteVo.PluginAttr plugin = plugins.getPluginAttr();
            if (plugin.getWhitelist() != null) {
                rateLimitingBo.setWhitelist(plugin.getWhitelist());
            }
            if (plugin.getBlacklist() != null) {
                rateLimitingBo.setBlacklist(plugin.getBlacklist());
            }
            rateLimitingBo.setEnabled(plugin.getEnabled() == null ? true : plugin.getEnabled());
            return rateLimitingBo;
        }

    };
}
