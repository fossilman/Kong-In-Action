package org.fibonacci.routeplus.handle.operate;

import com.alibaba.fastjson.JSONObject;
import org.fibonacci.framework.exceptions.ClientException;
import org.fibonacci.routeplus.constants.BaseConstants;
import org.fibonacci.routeplus.domain.ServicesRoutePlugin;
import org.fibonacci.routeplus.handle.api.ServicesRoutePluginApi;
import org.fibonacci.routeplus.helper.KongHelper;
import org.fibonacci.routeplus.mapper.ServicesRoutePluginMapper;
import org.fibonacci.routeplus.model.PluginVo;
import org.fibonacci.routeplus.model.RouteVo;
import org.fibonacci.routeplus.model.bo.*;
import org.fibonacci.routeplus.utils.RoutesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 黑白名单实现
 * @Author: zachary
 * @Date: 2020-05-26 12:05
 */
@Service
public class IpRestrictionPlugin implements ServicesRoutePluginApi {

    @Resource
    private KongHelper kongHelper;

    @Resource
    private ServicesRoutePluginMapper servicesRoutePluginMapper;


    /**
     * 黑白名单插件操作Kong
     *
     * @param pluginAsyncBo
     * @return
     */
    @Override
    public String execute(PluginAsyncBo pluginAsyncBo) {

        String pluginId = pluginAsyncBo.getPluginId();
        String[] whitelist = pluginAsyncBo.getRateLimitingBo().getWhitelist();
        String[] blacklist = pluginAsyncBo.getRateLimitingBo().getBlacklist();

        //构建参数
        LimitingPluginBo limitingPluginBo = new LimitingPluginBo();
        Map<String, Object> map = new HashMap<>();
        if (whitelist != null) {
            map.put("whitelist", whitelist);
        }
        if (blacklist != null) {
            map.put("blacklist", blacklist);
        }
        limitingPluginBo.setConfig(map);
        limitingPluginBo.setUpstreamName(pluginAsyncBo.getUpstreamName());
        limitingPluginBo.setEnabled(pluginAsyncBo.getRateLimitingBo().getEnabled());
        limitingPluginBo.setName(BaseConstants.PluginName.ipRestriction.getName());


        if (pluginId == null) {
            LimitingPluginDetailBo servicesPlugin = null;
            if (StringUtils.equalsIgnoreCase(pluginAsyncBo.getName(), BaseConstants.GatewayName.application.name())) {
                //新增
                servicesPlugin = kongHelper.createServicesPlugin(limitingPluginBo);
            } else {
                limitingPluginBo.setRouteType(pluginAsyncBo.getRouteType());
                servicesPlugin = kongHelper.createRoutePlugin(limitingPluginBo);
            }
            pluginId = servicesPlugin.getId();
        } else {

            LimitingPluginBo.LimitingPluginUpdateBo build = new LimitingPluginBo.LimitingPluginUpdateBo();
            build.setConfig(map);
            build.setEnabled(pluginAsyncBo.getRateLimitingBo().getEnabled());
            if (StringUtils.equalsIgnoreCase(pluginAsyncBo.getName(), BaseConstants.GatewayName.application.name())) {
                kongHelper.updateServicesPlugin(build, pluginId);
            } else {
                kongHelper.updateRoutePlugin(build, pluginId);
            }
        }
        return pluginId;

    }

    /**
     * 自定义黑白名单插件操作Kong
     *
     * @param routePluginAsyncBo
     * @return
     */
    @Override
    public Integer executeRoute(RoutePluginAsyncBo routePluginAsyncBo) {
        Integer dataId = routePluginAsyncBo.getDataId();
        RouteVo.Plugins plugin = routePluginAsyncBo.getPlugins();
        String serviceName = routePluginAsyncBo.getServiceName();
        Boolean hasServices = routePluginAsyncBo.getHasServices();
        Integer id = null;

        String[] whitelist = plugin.getPluginAttr().getWhitelist();
        String[] blacklist = plugin.getPluginAttr().getBlacklist();
        //黑白名单插件
        Map<String, Object> map = RoutesUtil.ipRestrictionMaps(whitelist, blacklist);

        if (plugin.getId() == null) {//新增

            PluginVo pluginVo = this.savePlugins(serviceName, map, dataId, hasServices);

            /*LimitingPluginBo build = new LimitingPluginBo();
            build.setServiceName(serviceName);
            build.setConfig(map);
            build.setEnabled(true);
            build.setName(BaseConstants.PluginName.ipRestriction.getName());

            LimitingPluginDetailBo servicesPlugin = null;
            Integer serviceId = null;
            Integer routeId = null;
            if (hasServices) {
                servicesPlugin = kongHelper.createServicesPlugin(build);
                serviceId = dataId;
            } else {
                servicesPlugin = kongHelper.createRoutePlugin(build);
                routeId = dataId;
            }*/

            RateLimitingBo rateLimitingBo = RateLimitingBo.builder()
                    .blacklist(blacklist).whitelist(whitelist)
                    .id(pluginVo.getId()).build();

            ServicesRoutePlugin servicesRoutePlugin = ServicesRoutePlugin.builder()
                    .serviceId(pluginVo.getServiceId()).routeId(pluginVo.getRouteId())
                    .pluginId(plugin.getPluginId()).pluginName(plugin.getPluginName())
                    .pluginConfig(JSONObject.toJSONString(rateLimitingBo)).build();
            servicesRoutePluginMapper.insertSelective(servicesRoutePlugin);
            id = servicesRoutePlugin.getId();
        } else {
            //修改
            ServicesRoutePlugin servicesRoutePlugin = servicesRoutePluginMapper.selectById(plugin.getId());
            if (servicesRoutePlugin == null) {
                throw new ClientException("10001", "插件不存在!");
            }

            RateLimitingBo rateLimitingBoz = JSONObject.parseObject(servicesRoutePlugin.getPluginConfig(), RateLimitingBo.class);
            //容错处理
            Boolean hasUpated = true;
            if (rateLimitingBoz.getId() == null) {
                //修改时插件不存在，新增插件
                PluginVo pluginVo = this.savePlugins(serviceName, map, dataId, hasServices);
                rateLimitingBoz.setId(pluginVo.getId());
                hasUpated = false;
            }

            if (whitelist != null) {
                rateLimitingBoz.setWhitelist(whitelist);
            }
            if (blacklist != null) {
                rateLimitingBoz.setBlacklist(blacklist);
            }

            if (plugin.getEnabled() != null) {
                servicesRoutePlugin.setEnabled(plugin.getEnabled());
            }

            servicesRoutePlugin.setPluginConfig(JSONObject.toJSONString(rateLimitingBoz));
            servicesRoutePluginMapper.updateByPrimaryKeySelective(servicesRoutePlugin);


            //修改插件
            if (hasUpated) {
                LimitingPluginBo.LimitingPluginUpdateBo build = new LimitingPluginBo.LimitingPluginUpdateBo();
                build.setConfig(map);
                if (plugin.getEnabled() != null) {
                    build.setEnabled(plugin.getEnabled());
                }
                if (hasServices) {
                    kongHelper.updateServicesPlugin(build, rateLimitingBoz.getId());
                } else {
                    kongHelper.updateRoutePlugin(build, rateLimitingBoz.getId());
                }
            }
            id = servicesRoutePlugin.getId();
        }
        return id;
    }

    /**
     * 黑白名单操作
     *
     * @param serviceRoutePluginBo
     * @return
     */
    @Override
    public Boolean enabledServicePlugin(ServiceRoutePluginBo serviceRoutePluginBo) {
        return null;
    }


    /***
     * @description: 保存插件
     * @param serviceName kong名称
     * @param map 插件属性
     * @param dataId 业务id
     * @param hasServices 是否业务层
     * @return {@link PluginVo}
     * @author: zachary
     * @date: 2020-06-03 13:37
     */
    private PluginVo savePlugins(String serviceName, Map map, Integer dataId, Boolean hasServices) {

        LimitingPluginBo build = new LimitingPluginBo();
        build.setServiceName(serviceName);
        build.setConfig(map);
        build.setEnabled(true);
        build.setName(BaseConstants.PluginName.ipRestriction.getName());

        LimitingPluginDetailBo servicesPlugin = null;
        Integer serviceId = null;
        Integer routeId = null;
        if (hasServices) {
            servicesPlugin = kongHelper.createServicesPlugin(build);
            serviceId = dataId;
        } else {
            servicesPlugin = kongHelper.createRoutePlugin(build);
            routeId = dataId;
        }

        if (servicesPlugin == null) {
            throw new ClientException("10003", "插件数据同步中，请稍后再试！");
        }

        return PluginVo.builder().id(servicesPlugin.getId()).routeId(routeId).serviceId(serviceId).build();
    }
}
