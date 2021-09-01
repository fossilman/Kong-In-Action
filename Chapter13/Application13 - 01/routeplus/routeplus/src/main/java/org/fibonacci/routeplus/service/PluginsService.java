package org.fibonacci.routeplus.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.fibonacci.framework.httpclient.HttpClientTemplate;
import org.fibonacci.routeplus.constants.KongConstants;
import org.fibonacci.routeplus.domain.ApplicationServices;
import org.fibonacci.routeplus.domain.Plugin;
import org.fibonacci.routeplus.domain.ServicesRoutePlugin;
import org.fibonacci.routeplus.domain.ServicesRoutes;
import org.fibonacci.routeplus.mapper.ApplicationServicesMapper;
import org.fibonacci.routeplus.mapper.PluginMapper;
import org.fibonacci.routeplus.mapper.ServicesRoutePluginMapper;
import org.fibonacci.routeplus.mapper.ServicesRoutesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class PluginsService {

    @Autowired
    private PluginMapper pluginMapper;
    @Autowired
    private ServicesRoutePluginMapper servicesRoutePluginMapper;
    @Autowired
    private ServicesRoutesMapper servicesRoutesMapper;
    @Autowired
    private ApplicationServicesMapper applicationServicesMapper;
    @Resource
    protected HttpClientTemplate httpClientTemplate;
    @Resource
    private KongConstants constants;

    private String getKongApiHost() {
        return constants.kongUrl;
    }

    public void addPlugin2Service(String kongServiceName, Integer pluginId, Object pluginConfig) {

        Plugin plugin = pluginMapper.selectByPrimaryKey(pluginId);
        Map<String, Object> addPluginRequest = new HashMap<>();
        addPluginRequest.put("name", plugin.getPluginName());
        addPluginRequest.put("config", pluginConfig);
        ApplicationServices applicationServices = applicationServicesMapper.getOneByKongServiceName(kongServiceName);

        String url = getKongApiHost() + "services/" + kongServiceName + "/plugins";
        String resp = httpClientTemplate.doPost(url, addPluginRequest);
        String name = JSON.parseObject(resp).getString("name");
        log.info("success");
        ServicesRoutePlugin servicesRoutePlugin = new ServicesRoutePlugin();
        servicesRoutePlugin.setEnabled(true);
        servicesRoutePlugin.setPluginConfig(JSON.toJSONString(pluginConfig));
        servicesRoutePlugin.setServiceId(applicationServices.getId());
        servicesRoutePlugin.setRouteId(null);
        servicesRoutePlugin.setPluginId(pluginId);
        servicesRoutePlugin.setPluginName(plugin.getPluginName());
        servicesRoutePluginMapper.insertSelective(servicesRoutePlugin);
    }

    public void deletePlugin2Service(Integer servicesRoutePluginId) {
        ServicesRoutePlugin servicesRoutePlugin = servicesRoutePluginMapper.selectById(servicesRoutePluginId);
        ApplicationServices applicationServices = applicationServicesMapper.selectByPrimaryKey(servicesRoutePlugin.getServiceId());
        String listAllPluginsUrl = getKongApiHost() + "services/" + applicationServices.getKongServicesName() + "/plugins";
        String allPluginsResp = httpClientTemplate.doGet(listAllPluginsUrl, null);
        List<Map<String, Object>> plugins = (List<Map<String, Object>>) JSON.parseObject(allPluginsResp).get("data");
        for (Map<String, Object> plugin : plugins) {
            String pluginName = servicesRoutePlugin.getPluginName();
            if(Objects.equals(pluginName, plugin.get("name"))) {
                String deletePluginUrl = getKongApiHost() + "/plugins/" + plugin.get("id");
                String resp = httpClientTemplate.doDelete(deletePluginUrl);
                log.info(resp);
                servicesRoutePluginMapper.deleteByPrimaryKey(servicesRoutePluginId);
            }
        }
    }

    public void addPlugin2Route(String kongRoutesName, Integer pluginId, Object pluginConfig) {
        ServicesRoutes servicesRoutes  = servicesRoutesMapper.getOneByKongRouteName(kongRoutesName);
        ApplicationServices applicationServices = applicationServicesMapper.selectByPrimaryKey(servicesRoutes.getServiceId());
        Plugin plugin = pluginMapper.selectByPrimaryKey(pluginId);
        Map<String, Object> addPluginRequest = new HashMap<>();
        addPluginRequest.put("name", plugin.getPluginName());
        addPluginRequest.put("config", pluginConfig);
        String url = getKongApiHost() + "routes/" + kongRoutesName + "/plugins";
        String resp = httpClientTemplate.doPost(url, addPluginRequest);
        String name = JSON.parseObject(resp).getString("name");
        log.info("success");
        ServicesRoutePlugin servicesRoutePlugin = new ServicesRoutePlugin();
        servicesRoutePlugin.setEnabled(true);
        servicesRoutePlugin.setPluginConfig(JSON.toJSONString(pluginConfig));
        servicesRoutePlugin.setServiceId(applicationServices.getId());
        servicesRoutePlugin.setRouteId(servicesRoutes.getId());
        servicesRoutePlugin.setPluginId(pluginId);
        servicesRoutePlugin.setPluginName(plugin.getPluginName());
        servicesRoutePluginMapper.insertSelective(servicesRoutePlugin);

    }

    public void deletePlugin2Route(Integer servicesRoutePluginId) {
        ServicesRoutePlugin servicesRoutePlugin = servicesRoutePluginMapper.selectById(servicesRoutePluginId);
        ServicesRoutes servicesRoutes = servicesRoutesMapper.selectByPrimaryKey(servicesRoutePlugin.getRouteId());
        String listAllPluginsUrl = getKongApiHost() + "routes/" + servicesRoutes.getKongRoutesName() + "/plugins";
        String allPluginsResp = httpClientTemplate.doGet(listAllPluginsUrl, null);
        List<Map<String, Object>> plugins = (List<Map<String, Object>>) JSON.parseObject(allPluginsResp).get("data");
        for (Map<String, Object> plugin : plugins) {
            String pluginName = servicesRoutePlugin.getPluginName();
            if(Objects.equals(pluginName, plugin.get("name"))) {
                String deletePluginUrl = getKongApiHost() + "/plugins/" + plugin.get("id");
                String resp = httpClientTemplate.doDelete(deletePluginUrl);
                log.info(resp);
                servicesRoutePluginMapper.deleteByPrimaryKey(servicesRoutePluginId);
            }
        }
    }




}
