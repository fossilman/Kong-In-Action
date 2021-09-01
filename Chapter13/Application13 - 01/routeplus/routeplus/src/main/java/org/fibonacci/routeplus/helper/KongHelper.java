package org.fibonacci.routeplus.helper;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.fibonacci.framework.exceptions.ClientException;
import org.fibonacci.framework.exceptions.HttpClientException;
import org.fibonacci.framework.exceptions.ServerException;
import org.fibonacci.framework.httpclient.HttpClientTemplate;
import org.fibonacci.routeplus.common.bo.TargetBo;
import org.fibonacci.routeplus.common.bo.UpstreamBo;
import org.fibonacci.routeplus.common.vo.KongStatusVo;
import org.fibonacci.routeplus.common.vo.KongVo;
import org.fibonacci.routeplus.configuration.Configs;
import org.fibonacci.routeplus.constants.BaseConstants;
import org.fibonacci.routeplus.constants.KongConstants;
import org.fibonacci.routeplus.handle.PluginOperate;
import org.fibonacci.routeplus.model.bo.*;
import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author ：zachary
 * @description： Kong的帮助类
 * @date ：Created in 2019-09-17 14:51
 */
@Component
@Slf4j
public class KongHelper {

    private KongHelper() {
    }

    @Resource
    private KongConstants kongConstants;

    @Resource
    protected HttpClientTemplate httpClientTemplate;

    @Resource
    private Configs configs;


    @Resource
    private PluginOperate rateLimitingPluginOperate;

    @Resource
    private PluginOperate ipRestrictionPluginOperate;

    /**
     * 新增或者保存插件
     *
     * @param pluginAsyncBo
     * @return
     */
    public String saveOrUpdateKongPlugin(PluginAsyncBo pluginAsyncBo) {
        String pluginId = null;
        if (StringUtils.equals(pluginAsyncBo.getPluginName(), BaseConstants.PluginName.rateLimiting.name())) {
            pluginId = rateLimitingPluginOperate.execute(pluginAsyncBo);
        } else if (StringUtils.equals(pluginAsyncBo.getPluginName(), BaseConstants.PluginName.ipRestriction.name())) {
            pluginId = ipRestrictionPluginOperate.execute(pluginAsyncBo);
        } else {
            throw new ClientException("10001", "插件类型未开放!");
        }
        return pluginId;
    }


    /**
     * 新增或者保存自定义插件
     *
     * @param routePluginAsyncBo
     * @return
     */
    public Integer saveOrUpdateMyselfPlugin(RoutePluginAsyncBo routePluginAsyncBo) {
        Integer pluginId = null;
        if (StringUtils.equals(routePluginAsyncBo.getPluginName(), BaseConstants.PluginName.rateLimiting.name())) {
            pluginId = rateLimitingPluginOperate.executeRoute(routePluginAsyncBo);
        } else if (StringUtils.equals(routePluginAsyncBo.getPluginName(), BaseConstants.PluginName.ipRestriction.name())) {
            pluginId = ipRestrictionPluginOperate.executeRoute(routePluginAsyncBo);
        } else {
            throw new ClientException("10001", "插件类型未开放!");
        }
        return pluginId;
    }


    /**
     * 检查kong状态
     */
    public KongStatusVo checkKongStatus() {

        String kongUrl = kongConstants.kongStatus;

        try {
            String result = httpClientTemplate.doGet(kongUrl);
            if (result != null) {
                return JSONObject.parseObject(result, KongStatusVo.class);
            }
        } catch (HttpClientException e) {
            log.info("检查kong失败", e);
        }
        return null;
    }


    /**
     * 获取upstream
     */
    public UpstreamBo getUpstream(String kongName) {

        Assert.notNull(kongName);
        String kongNameUrl = kongConstants.kongUpstreamName.replace("@name", kongName);
        String result = null;
        try {
            result = httpClientTemplate.doGet(kongNameUrl);
            if (result != null) {
                return JSONObject.parseObject(result, UpstreamBo.class);
            }
        } catch (HttpClientException e) {
            log.info("获取kong失败", e);
        }
        return null;
    }

    /**
     * 创建kong的Upstream信息
     *
     * @param kongName
     * @return
     */
    public UpstreamBo createKongUpstream(String kongName) {

        Assert.notNull(kongName);
        UpstreamBo upstreamBo = UpstreamBo.builder().name(kongName).
                healthchecks(UpstreamBo.HealthchecksBean.builder().
                        active(UpstreamBo.HealthchecksBean.ActiveBean.builder().
                                http_path("/" + kongName + "/home").timeout(1).concurrency(10).
                                healthy(UpstreamBo.HealthchecksBean.ActiveBean.HealthyBean.builder().
                                        interval(3).successes(1).build()).
                                unhealthy(UpstreamBo.HealthchecksBean.ActiveBean.UnhealthyBean.builder().
                                        interval(3).timeouts(30).build()).build()).build()).build();


        String url = kongConstants.kongUpstream;
        try {
            String result = httpClientTemplate.doPost(url, upstreamBo);
            if (result == null) {
                log.info("创建kong的Upstream信息失败");
                throw new ServerException("-1", "创建kong的Upstream信息失败");
            }
        } catch (HttpClientException e) {
            log.info("创建kong-upstream失败", e);
        }
        return upstreamBo;
    }

    /**
     * 修改kong的Upstream信息
     *
     * @param kongName
     * @return
     */
    public UpstreamBo updateKongUpstream(String kongName) {

        Assert.notNull(kongName);


        UpstreamBo upstream = UpstreamBo.builder().name(kongName).
                healthchecks(UpstreamBo.HealthchecksBean.builder().
                        active(UpstreamBo.HealthchecksBean.ActiveBean.builder().
                                http_path("/" + kongName + "/home").timeout(1).concurrency(10).
                                healthy(UpstreamBo.HealthchecksBean.ActiveBean.HealthyBean.builder().
                                        interval(3).successes(1).build()).
                                unhealthy(UpstreamBo.HealthchecksBean.ActiveBean.UnhealthyBean.builder().
                                        interval(3).timeouts(30).build()).build()).build()).build();

        String url = kongConstants.kongUpstreamName.replace("@name", kongName);

        try {
            String result = httpClientTemplate.doPut(url, upstream);
            if (result == null) {
                log.info("修改kong的Upstream信息失败");
                throw new ServerException("-1", "创建kong的Upstream信息失败");
            }
        } catch (HttpClientException e) {
            log.info("修改kong-upstream失败", e);
        }
        return upstream;
    }

    /**
     * 删除kong的Upstream信息
     *
     * @param kongName
     * @return
     */
    public void delUpstream(String kongName) {
        String kongTargetUrl = kongConstants.kongUpstreamName.replace("@name", kongName);
        try {
            httpClientTemplate.doDelete(kongTargetUrl);
        } catch (HttpClientException e) {
            log.info("删除kong-upstream失败", e);
        }
    }


    /**
     * 获取upstream/target
     */
    public KongVo getTarget(String kongName) {

        Assert.notNull(kongName);
        String targetUrl = kongConstants.kongTarget.replace("@name", kongName);

        try {
            String result = httpClientTemplate.doGet(targetUrl);
            if (result != null) {
                return JSONObject.parseObject(result, KongVo.class);
            }
        } catch (HttpClientException e) {
            log.info("获取kong-target失败", e);
        }
        return null;

    }


    /**
     * 新增upstream/target
     */
    public TargetBo createTarget(TargetBo targetBo) {

        Assert.notNull(targetBo);
        String kongTargetUrl = kongConstants.kongTargetSave.replace("@upstreamname", targetBo.getUpstreamName());
        targetBo.setUpstreamName(null);
        try {
            String result = httpClientTemplate.doPost(kongTargetUrl, targetBo);
            if (result != null) {
                return targetBo;
            }
        } catch (HttpClientException e) {
            log.info("创建kong-target失败", e);
        }
        return null;
    }

    /**
     * 创建kong的Upstream信息
     *
     * @param kongName
     * @return
     */
    public void delTarget(String kongName, String target) {
        String kongTargetUrl = kongConstants.kongTargetDel.replace("@upstreamname", kongName).replace("@hosts", target);
        try {
            httpClientTemplate.doDelete(kongTargetUrl);
        } catch (HttpClientException e) {
            log.info("删除kong-target失败", e);
        }
    }


    /**
     * 获取services
     */
    public KongVo.ServicesDetail getServices(String serviceName) {

        Assert.notNull(serviceName);
        String result = null;
        try {
            String servicesUrl = kongConstants.kongServices + "/" + serviceName;
            result = httpClientTemplate.doGet(servicesUrl);
            if (result != null) {
                return JSONObject.parseObject(result, KongVo.ServicesDetail.class);
            }
        } catch (HttpClientException e) {
            //log.info("获取services失败",e);
            log.info("获取services失败");
        }
        return null;
    }

    /**
     * 创建services
     */
    public KongServiceBo createServices(KongServiceBo serviceBo) {
        Assert.notNull(serviceBo);

        String servicesUrl = kongConstants.kongServices;
        try {
            String result = httpClientTemplate.doPost(servicesUrl, serviceBo);
            if (result != null) {
                return JSONObject.parseObject(result, KongServiceBo.class);
            }
        } catch (HttpClientException e) {
            log.info("创建services失败", e);
        }
        return null;
    }

    /**
     * 删除 services
     *
     * @param serviceId
     * @return
     */
    public void delServices(String serviceId) {
        String serviceUrl = kongConstants.kongServicesDel.replace("@id", serviceId);
        try {
            httpClientTemplate.doDelete(serviceUrl);
        } catch (HttpClientException e) {
            log.info("删除services失败", e);
        }
    }


    /**
     * 查询routes
     */
    public KongVo.RoutesDetail getRoutes(String kongId) {
        Assert.notNull(kongId);

        String routeUrl = kongConstants.kongRoutes.replace("@servicesnames", kongId);
        try {
            String result = httpClientTemplate.doGet(routeUrl);
            if (result != null) {
                return JSONObject.parseObject(result, KongVo.RoutesDetail.class);
            }
        } catch (HttpClientException e) {
            //log.info("获取routes失败", e);
            log.info("获取routes失败");
        }
        return null;

    }

    /**
     * 查询routes详情
     */
    public KongRouteBo getRouteDetail(String routeName) {
        Assert.notNull(routeName);

        String routeUrl = kongConstants.kongRoutesDetail.replace("@routenames", routeName);
        try {
            String result = httpClientTemplate.doGet(routeUrl);
            if (result != null) {
                return JSONObject.parseObject(result, KongRouteBo.class);
            }
        } catch (HttpClientException e) {
            //log.info("获取routes详情失败", e);
            log.info("获取routes详情失败");
        }
        return null;
    }


    /**
     * 创建routes
     */
    public KongRouteBo createRoutes(KongRouteBo routeBo) {
        Assert.notNull(routeBo);

        String routeUrl = kongConstants.kongRouteslist;
        try {
            String result = httpClientTemplate.doPost(routeUrl, routeBo);
            if (result != null) {
                return JSONObject.parseObject(result, KongRouteBo.class);
            }
        } catch (HttpClientException e) {
            log.info("创建routes失败", e);
        }
        return null;
    }

    /**
     * 修改routes
     */
    public KongRouteBo updateRoutes(KongRouteBo routeBo) {
        Assert.notNull(routeBo);

        String routeUrl = kongConstants.kongRouteUpdate.replace("@name", routeBo.getName());
        try {
            String result = httpClientTemplate.doPatch(routeUrl, routeBo, SerializerFeature.DisableCircularReferenceDetect);
            if (result != null) {
                return routeBo;
            }
        } catch (HttpClientException e) {
            log.info("更新routes失败", e);
        }
        return null;
    }

    /**
     * 删除 routes
     *
     * @param routeId
     * @return
     */
    public void delRoutes(String routeId) {
        String routesUrl = kongConstants.kongRoutesDel.replace("@id", routeId);
        try {
            httpClientTemplate.doDelete(routesUrl);
        } catch (HttpClientException e) {
            log.info("删除routes失败", e);
        }

    }


    /**
     * 创建services的插件
     */
    public LimitingPluginDetailBo createServicesPlugin(LimitingPluginBo limitingPluginBo) {
        Assert.notNull(limitingPluginBo);

        String servicesName = limitingPluginBo.getServiceName();
        if (servicesName == null) {
            servicesName = configs.getSname().replace("@servicename", limitingPluginBo.getUpstreamName());
        }
        String servicesUrl = kongConstants.kongServicesPlugin.replace("@servicename", servicesName);
        limitingPluginBo.setUpstreamName(null);
        limitingPluginBo.setServiceName(null);
        try {
            String result = httpClientTemplate.doPost(servicesUrl, limitingPluginBo);
            if (result != null) {
                return JSONObject.parseObject(result, LimitingPluginDetailBo.class);
            }
        } catch (HttpClientException e) {
            log.info("创建services-plugin失败", e);
        }
        return null;
    }

    /**
     * 修改services的插件
     */
    public LimitingPluginBo.LimitingPluginUpdateBo updateServicesPlugin(LimitingPluginBo.LimitingPluginUpdateBo
                                                                                limitingPluginUpdateBo,
                                                                        String pluginId) {
        Assert.notNull(limitingPluginUpdateBo);
        Assert.notNull(pluginId);

        String servicesUrl = kongConstants.kongPlugin.replace("@pluginid", pluginId);
        try {
            String result = httpClientTemplate.doPatch(servicesUrl, limitingPluginUpdateBo);
            if (result != null) {
                return limitingPluginUpdateBo;
            }
        } catch (HttpClientException e) {
            log.info("更新services-plugin失败", e);
        }
        return null;
    }

    /**
     * 获取services的插件
     */
    public ServicePluginDetailBo getServicePlugin(LimitingPluginBo limitingPluginBo) {
        Assert.notNull(limitingPluginBo);

        String servicesName = configs.getSname().replace("@servicename", limitingPluginBo.getUpstreamName());
        String servicesUrl = kongConstants.kongServicesPlugin.replace("@servicename", servicesName)
                .concat("/")
                .concat(limitingPluginBo.getPluginId());
        try {
            String result = httpClientTemplate.doGet(servicesUrl);
            if (result != null) {
                return JSONObject.parseObject(result, ServicePluginDetailBo.class);
            }
        } catch (HttpClientException e) {
            log.info("获取services-plugin失败", e);
        }
        return null;
    }

    /**
     * 修改services的插件
     */
    public void deleteServicesPlugin(LimitingPluginBo limitingPluginBo) {
        Assert.notNull(limitingPluginBo);
        Assert.notNull(limitingPluginBo.getPluginId());
        String servicesUrl = kongConstants.kongPlugin.replace("@pluginid", limitingPluginBo.getPluginId());
        try {
            httpClientTemplate.doDelete(servicesUrl);
        } catch (HttpClientException e) {
            log.info("删除services-plugin失败", e);
        }

    }


    /**
     * 创建route的插件
     */
    public LimitingPluginDetailBo createRoutePlugin(LimitingPluginBo limitingPluginBo) {
        Assert.notNull(limitingPluginBo);

        String servicesName = limitingPluginBo.getServiceName();
        if (servicesName == null) {
            servicesName = configs.getRname().replace("@routename", limitingPluginBo.getUpstreamName()).
                    replace("@type", limitingPluginBo.getRouteType());
        }
        String routeUrl = kongConstants.kongRoutePlugin.replace("@routename", servicesName);
        limitingPluginBo.setUpstreamName(null);
        limitingPluginBo.setRouteType(null);
        limitingPluginBo.setServiceName(null);
        try {
            String result = httpClientTemplate.doPost(routeUrl, limitingPluginBo);
            if (result != null) {
                return JSONObject.parseObject(result, LimitingPluginDetailBo.class);
            }
        } catch (HttpClientException e) {
            log.info("创建routes-plugin失败", e);
        }
        return null;
    }

    /**
     * 修改route的插件
     */
    public LimitingPluginBo.LimitingPluginUpdateBo updateRoutePlugin(LimitingPluginBo.LimitingPluginUpdateBo
                                                                             limitingPluginUpdateBo, String pluginId) {
        Assert.notNull(limitingPluginUpdateBo);
        Assert.notNull(pluginId);

        String servicesUrl = kongConstants.kongPlugin.replace("@pluginid", pluginId);
        try {
            String result = httpClientTemplate.doPatch(servicesUrl, limitingPluginUpdateBo);
            if (result != null) {
                return limitingPluginUpdateBo;
            }
        } catch (HttpClientException e) {
            log.info("更新routes-plugin失败", e);
        }
        return null;
    }

    /**
     * 获取Route的插件
     */
    public RoutePluginDetailBo getRoutePlugin(LimitingPluginBo limitingPluginBo) {
        Assert.notNull(limitingPluginBo);

        String routeName = configs.getRname().replace("@routename", limitingPluginBo.getUpstreamName()).
                replace("@type", limitingPluginBo.getRouteType());
        String routeUrl = kongConstants.kongServicesPlugin.replace("@routename", routeName)
                .concat("/")
                .concat(limitingPluginBo.getPluginId());
        try {
            String result = httpClientTemplate.doGet(routeUrl);
            if (result != null) {
                return JSONObject.parseObject(result, RoutePluginDetailBo.class);
            }
        } catch (HttpClientException e) {
            log.info("获取routes-plugin失败", e);
        }
        return null;
    }

    /**
     * 修改services的插件
     */
    public void deleteRoutePlugin(LimitingPluginBo limitingPluginBo) {
        Assert.notNull(limitingPluginBo);
        Assert.notNull(limitingPluginBo.getPluginId());
        String routeUrl = kongConstants.kongPlugin.replace("@pluginid", limitingPluginBo.getPluginId());
        try {
            httpClientTemplate.doDelete(routeUrl);
        } catch (HttpClientException e) {
            log.info("删除routes-plugin失败", e);
        }
    }
}
