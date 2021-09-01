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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 限流插件实现
 * @Author: zachary
 * @Date: 2020-05-26 12:05
 */
@Service
public class RateLimitingPlugin implements ServicesRoutePluginApi {


    @Resource
    private KongHelper kongHelper;

    @Resource
    private ServicesRoutePluginMapper servicesRoutePluginMapper;


    /**
     * 限流插件操作Kong
     *
     * @param pluginAsyncBo
     * @return
     */
    @Override
    public String execute(PluginAsyncBo pluginAsyncBo) {

        String pluginId = pluginAsyncBo.getPluginId();
        String period = pluginAsyncBo.getRateLimitingBo().getPeriod();
        Integer quantity = pluginAsyncBo.getRateLimitingBo().getQuantity();

        //构建参数
        LimitingPluginBo limitingPluginBo = new LimitingPluginBo();
        Map<String, Object> periods = BaseConstants.Period.getPeriods(period, quantity);
        limitingPluginBo.setConfig(periods);
        limitingPluginBo.setUpstreamName(pluginAsyncBo.getUpstreamName());
        limitingPluginBo.setEnabled(pluginAsyncBo.getRateLimitingBo().getEnabled());
        limitingPluginBo.setServiceName(pluginAsyncBo.getGName());

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
            build.setConfig(periods);
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
     * 自定义限流插件操作Kong
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
        if (plugin.getId() == null) {//新增
            //TODO 处理kong插件新增
            PluginVo pluginVo = this.savePlugins(serviceName, plugin, dataId, hasServices);
            /*Map map = new HashMap();
            map.put(plugin.getPluginAttr().getPeriod(), plugin.getPluginAttr().getQuantity());
            LimitingPluginBo build = new LimitingPluginBo();
            build.setServiceName(serviceName);
            build.setConfig(map);
            build.setEnabled(true);

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
                    .period(plugin.getPluginAttr().getPeriod())
                    .quantity(plugin.getPluginAttr().getQuantity())
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

            Boolean hasUpated = true;
            RateLimitingBo rateLimitingBoz = JSONObject.parseObject(servicesRoutePlugin.getPluginConfig(), RateLimitingBo.class);
            if (rateLimitingBoz.getId() == null) {
                PluginVo pluginVo = this.savePlugins(serviceName, plugin, dataId, hasServices);
                rateLimitingBoz.setId(pluginVo.getId());
            }

            if (plugin.getPluginAttr().getQuantity() != null) {
                rateLimitingBoz.setQuantity(plugin.getPluginAttr().getQuantity());
            }
            if (plugin.getPluginAttr().getPeriod() != null) {
                rateLimitingBoz.setPeriod(plugin.getPluginAttr().getPeriod());
            }

            if (plugin.getEnabled() != null) {
                servicesRoutePlugin.setEnabled(plugin.getEnabled());
            }

            servicesRoutePlugin.setPluginConfig(JSONObject.toJSONString(rateLimitingBoz));
            servicesRoutePluginMapper.updateByPrimaryKeySelective(servicesRoutePlugin);

            //修改插件
            if (hasUpated) {
                Map<String, Object> periods = BaseConstants.Period.getPeriods(rateLimitingBoz.getPeriod(), rateLimitingBoz.getQuantity());
                LimitingPluginBo.LimitingPluginUpdateBo build = new LimitingPluginBo.LimitingPluginUpdateBo();
                build.setConfig(periods);
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
     * 禁用插件
     *
     * @param serviceRoutePluginBo
     * @return
     */
    @Override
    public Boolean enabledServicePlugin(ServiceRoutePluginBo serviceRoutePluginBo) {

        List<ServicesRoutePlugin> servicesRoutePlugins = serviceRoutePluginBo.getServicesRoutePlugins();
        if (servicesRoutePlugins != null) {
            String kongRoutesName = serviceRoutePluginBo.getKongRoutesName();
            for (ServicesRoutePlugin servicesRoutePlugin : servicesRoutePlugins) {
                RateLimitingBo rateLimitingBo = JSONObject.parseObject(servicesRoutePlugin.getPluginConfig(), RateLimitingBo.class);
                //创建插件
                LimitingPluginBo limitingPluginBo = new LimitingPluginBo();
                limitingPluginBo.setServiceName(kongRoutesName);
                Map<String, Object> periods = BaseConstants.Period.getPeriods(rateLimitingBo.getPeriod(), rateLimitingBo.getQuantity());
                limitingPluginBo.setConfig(periods);
                LimitingPluginDetailBo limitingPluginDetailBo = kongHelper.createRoutePlugin(limitingPluginBo);
                if (limitingPluginDetailBo != null) {
                    //修改routes的插件的Id
                    rateLimitingBo.setId(limitingPluginDetailBo.getId());
                    servicesRoutePlugin.setPluginConfig(JSONObject.toJSONString(rateLimitingBo));
                    servicesRoutePlugin.setEnabled(true);
                    servicesRoutePluginMapper.updateByPrimaryKeySelective(servicesRoutePlugin);
                }
            }
        }
        return null;
    }

    /***
     * @description: 保存插件
     * @param serviceName kong名称
     * @param plugin 插件属性
     * @param dataId 业务id
     * @param hasServices 是否业务层
     * @return {@link PluginVo}
     * @author: zachary
     * @date: 2020-06-03 13:37
     */
    private PluginVo savePlugins(String serviceName, RouteVo.Plugins plugin, Integer dataId, Boolean hasServices) {

        Map map = new HashMap();
        map.put(plugin.getPluginAttr().getPeriod(), plugin.getPluginAttr().getQuantity());
        LimitingPluginBo build = new LimitingPluginBo();
        build.setServiceName(serviceName);
        build.setConfig(map);
        build.setEnabled(true);

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
