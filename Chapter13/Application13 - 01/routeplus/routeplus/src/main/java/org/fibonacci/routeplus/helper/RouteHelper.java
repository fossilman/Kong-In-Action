package org.fibonacci.routeplus.helper;

import com.alibaba.fastjson.JSONObject;
import org.fibonacci.framework.exceptions.ClientException;
import org.fibonacci.routeplus.configuration.Configs;
import org.fibonacci.routeplus.constants.PluginEnum;
import org.fibonacci.routeplus.domain.ServicesRoutePlugin;
import org.fibonacci.routeplus.mapper.ServicesRoutePluginMapper;
import org.fibonacci.routeplus.model.RouteVo;
import org.fibonacci.routeplus.model.bo.RateLimitingBo;
import org.fibonacci.routeplus.model.bo.RouteBo;
import org.fibonacci.routeplus.model.bo.RoutePluginAsyncBo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ：zachary
 * @description：
 * @date ：Created in 2020-05-09 16:50
 */
@Component
@Slf4j
public class RouteHelper {


    @Resource
    private ServicesRoutePluginMapper servicesRoutePluginMapper;

    @Resource
    private KongHelper kongHelper;

    @Resource
    private Configs configs;


    /**
     * 合并对象
     */
    public List<Integer> combineIds(RouteBo routeBo) {
        RouteBo.RouteInnerService services = routeBo.getServices();
        RouteBo.InnerRoute routes = routeBo.getRoutes();
        if (services == null && routes == null) {
            throw new ClientException("100001", "禁用参数为空!");
        }

        List<Integer> ids = new ArrayList<>();
        if (services != null) {
            ids.add(services.getId());
        }
        if (routes != null) {
            ids.add(routes.getId());
        }
        return ids;
    }

    /**
     * 获取插件类型
     */
    public RouteVo.Plugins getLimitingPlugin(List<RouteVo.Plugins> plugins) {

        if (plugins != null) {
            return plugins.get(0);
            /*Map<String, List<RouteVo.Plugins>> collect = plugins.stream().collect(Collectors.groupingBy(RouteVo.Plugins::getPluginName));
            List<RouteVo.Plugins> pluginsList = collect.get(pluginName);
            if (pluginsList != null) {
                return pluginsList.get(0);
            }*/
        }
        return null;
    }

    /**
     * 获取匹配插件类型
     */
    public RateLimitingBo generRateLimiting(RouteVo.Plugins plugin) {
        RateLimitingBo rateLimitingBo = null;
        try {
            rateLimitingBo = PluginEnum.valueOf(plugin.getPluginName()).mapping(plugin);
        } catch (IllegalArgumentException e) {
            throw new ClientException("10001", "插件不存在！");
        }
        return rateLimitingBo;
    }


    /**
     * @param dataId      （service或者route 业务主键id）
     * @param plugins     插件列表
     * @param serviceName 服务Kong名称
     * @param hasServices service或者route
     * @return
     */
    public Integer executeRoutePlugin(Integer dataId, List<RouteVo.Plugins> plugins,
                                      String serviceName, Boolean hasServices) {

        RouteVo.Plugins plugin = this.getLimitingPlugin(plugins);
        if (plugin == null) {
            return null;
        }
        //Integer id = null;
        if (plugin.getPluginAttr() == null) {
            throw new ClientException("10001", "插件内容为空!");
        }

        //实例化
        RoutePluginAsyncBo routePluginAsyncBo = new RoutePluginAsyncBo();
        routePluginAsyncBo.setDataId(dataId);
        routePluginAsyncBo.setPlugins(plugin);
        routePluginAsyncBo.setServiceName(serviceName);
        routePluginAsyncBo.setHasServices(hasServices);
        routePluginAsyncBo.setPluginName(plugin.getPluginName());
        return kongHelper.saveOrUpdateMyselfPlugin(routePluginAsyncBo);
    }


    /***
     * 插件列表
     */
    public List<RouteVo.Plugins> routeList(List<ServicesRoutePlugin> routePluginList) {

        //网关本身插件
        List<RouteVo.Plugins> plugins = new ArrayList<>();
        if (routePluginList != null) {
            for (ServicesRoutePlugin routePlugin : routePluginList) {
                RouteVo.Plugins build = RouteVo.Plugins.builder().id(routePlugin.getId()).
                        pluginId(routePlugin.getPluginId()).pluginName(routePlugin.getPluginName()).enabled(routePlugin.getEnabled()).build();
                if (!StringUtils.isBlank(routePlugin.getPluginConfig())) {
                    RateLimitingBo rateLimitingBo = JSONObject.parseObject(routePlugin.getPluginConfig(), RateLimitingBo.class);
                    build.setPluginAttr(RouteVo.PluginAttr.builder()
                            .quantity(rateLimitingBo.getQuantity()).period(rateLimitingBo.getPeriod())
                            .whitelist(rateLimitingBo.getWhitelist()).blacklist(rateLimitingBo.getBlacklist())
                            .build());
                }
                plugins.add(build);
            }
        }
        return plugins;
    }


    /**
     * 获取自定义名称
     *
     * @param path            （接口请求地址）
     * @param applicationName （应用名称）
     */
    public String getServiceName(String path, String applicationName) {

        if (StringUtils.isBlank(path)) {
            return null;
        }

        String[] splits = path.split("/");
        String serviceName = configs.getSname().replace("@servicename", applicationName);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(serviceName);
        for (String split : splits) {
            if (StringUtils.isNotBlank(split) && !StringUtils.equalsIgnoreCase(applicationName, split)) {
                stringBuffer.append("_").append(split);
            }
        }

        return stringBuffer.toString();
    }


    /**
     * 获取自定义名称
     * 格式：/openapi/openapi_vcode_home
     * @param path            （接口请求地址）
     * @param applicationName （应用名称）
     * @param routeType       （应用类型）
     */
    public String getRouteName(String path, String applicationName, String routeType) {

        if (StringUtils.isBlank(path)) {
            return null;
        }

        String[] splits = path.split("/");
        String routeName = configs.getRname().replace("@routename", applicationName).replace("@type", routeType);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(routeName);
        for (String split : splits) {
            if (StringUtils.isNotBlank(split) && !StringUtils.equalsIgnoreCase(applicationName, split)) {
                stringBuffer.append("_").append(split);
            }
        }
        return stringBuffer.toString();
    }


    /**
     * 获取外部路径
     * 格式:/openapi/openapi_vcode/vcode/home
     *
     * @param path            （接口请求地址）
     * @param applicationName （应用名称）
     * @param routeType       （应用类型）
     */// FIXME TODO kun.li outpath == path
    public String getRoutePath(String path, String applicationName, String routeType) {

        String outpath = configs.getRpath().replace("@type", routeType).replace("@path", applicationName);
        String[] splits = path.split("/");
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(outpath);
        for (String split : splits) {
            if (StringUtils.isNotBlank(split)) {
                stringBuffer.append("/").append(split);
            }
        }
        return stringBuffer.toString();
    }
}
