package org.fibonacci.routeplus.async;

import com.alibaba.fastjson.JSONObject;
import org.fibonacci.routeplus.common.bo.RoutePlusBo;
import org.fibonacci.routeplus.common.bo.TargetBo;
import org.fibonacci.routeplus.common.bo.UpstreamBo;
import org.fibonacci.routeplus.common.vo.KongVo;
import org.fibonacci.routeplus.configuration.Configs;
import org.fibonacci.routeplus.constants.BaseConstants;
import org.fibonacci.routeplus.domain.ApplicationServices;
import org.fibonacci.routeplus.domain.GatewayPlugin;
import org.fibonacci.routeplus.domain.ServicesRoutePlugin;
import org.fibonacci.routeplus.domain.ServicesRoutes;
import org.fibonacci.routeplus.helper.KongHelper;
import org.fibonacci.routeplus.helper.RouteHelper;
import org.fibonacci.routeplus.mapper.ApplicationServicesMapper;
import org.fibonacci.routeplus.mapper.GatewayPluginMapper;
import org.fibonacci.routeplus.mapper.ServicesRoutePluginMapper;
import org.fibonacci.routeplus.mapper.ServicesRoutesMapper;
import org.fibonacci.routeplus.model.bo.*;
import org.fibonacci.routeplus.utils.RoutesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author ：zachary
 * @description： Kong异步处理
 * @date ：Created in 2019-08-14 11:37
 */
@Slf4j
@Component
public class KongAsync {


    @Resource
    private KongHelper kongHelper;

    @Resource
    private Configs configs;

    @Resource
    private ServicesRoutePluginMapper servicesRoutePluginMapper;

    @Resource
    private ApplicationServicesMapper applicationServicesMapper;

    @Resource
    private ServicesRoutesMapper servicesRoutesMapper;

    @Resource
    private RouteHelper routeHelper;


    @Resource
    private GatewayPluginMapper gatewayPluginMapper;

    /**
     * 异步处理kong的插件（services/新增/修改，routes/新增/修改）
     *
     * @param pluginAsyncBo
     */
    @Async("taskExecutor")
    public void executeGatewayPlugin(PluginAsyncBo pluginAsyncBo) {

        String pluginId = kongHelper.saveOrUpdateKongPlugin(pluginAsyncBo);

        //处理业务,更新kong的id
        if (pluginAsyncBo.getPluginId() == null) {
            GatewayPlugin gatewayPlugin = gatewayPluginMapper.selectByPrimaryKey(pluginAsyncBo.getId());
            if (gatewayPlugin != null && gatewayPlugin.getPluginConfig() != null) {
                RateLimitingBo rateLimitingBo = JSONObject.parseObject(gatewayPlugin.getPluginConfig(), RateLimitingBo.class);
                rateLimitingBo.setId(pluginId);
                gatewayPlugin.setPluginConfig(JSONObject.toJSONString(rateLimitingBo));
                gatewayPluginMapper.updateByPrimaryKeySelective(gatewayPlugin);
            }
        }

    }


    /**
     * @param pluginAsyncBo
     * @desc 禁止插件(services, routes ）
     */
    @Async("taskExecutor")
    public void enabledKongPlugin(PluginAsyncBo pluginAsyncBo) {

        LimitingPluginBo.LimitingPluginUpdateBo build = new LimitingPluginBo.LimitingPluginUpdateBo();
        build.setEnabled(pluginAsyncBo.getEnabled());
        String pluginId = pluginAsyncBo.getPluginId();
        if (StringUtils.equalsIgnoreCase(pluginAsyncBo.getName(), BaseConstants.GatewayName.application.name())) {
            kongHelper.updateServicesPlugin(build, pluginId);
        } else {
            kongHelper.updateRoutePlugin(build, pluginId);
        }
    }

    /**
     * @param pluginAsyncBo
     * @desc 删除插件(services, routes ）
     */
    @Async("taskExecutor")
    public void deleteKongPlugin(PluginAsyncBo pluginAsyncBo) {

        LimitingPluginBo limitingPluginBo = LimitingPluginBo.builder()
                .pluginId(pluginAsyncBo.getPluginId())
                .build();
        if (StringUtils.equalsIgnoreCase(pluginAsyncBo.getName(), BaseConstants.GatewayName.application.name())) {
            kongHelper.deleteServicesPlugin(limitingPluginBo);
        } else {
            kongHelper.deleteRoutePlugin(limitingPluginBo);
        }
    }


    /**
     * 异步处理kong逻辑（更新routes）
     * 修改kong的外部路径
     * 修改kong插件的属性
     *
     * @param serviceBo
     * @param kongRouteName kong路由名称
     */
    @Async("taskExecutor")
    public void exeRoutePlugin(ServiceBo serviceBo, String kongRouteName) {

        Integer id = serviceBo.getRoutesPool().getId();
        String outPath = serviceBo.getRoutesPool().getOutPath();
        if (!outPath.startsWith("/")) {
            outPath = "/" + outPath;
        }
        if (id != null && outPath != null) {
            //outPath = (outPath.replace(path, ""));
            //修改kong 的外部路径
            //String routeName = configs.getRname().replace("@routename", upstreamName).replace("@type", servicesRoutes.getRouteType());
            KongRouteBo kongRouteBo = KongRouteBo.builder()
                    //.service(KongRouteBo.ServiceBean.builder().id(servicesRoutes.getk()).build())
                    //.name("route_" + upstream.getName() + "_web")
                    //.name(routeName)
                    //.paths(new String[]{"/web/web_" + upstream.getName()})
                    .paths(new String[]{outPath})
                    .build();
            kongRouteBo.setName(kongRouteName);
            kongHelper.updateRoutes(kongRouteBo);
        }

    }


    /**
     * @param applicationServices 应用层对象
     * @param enabled             是否启用
     * @param updateName          更新人名称
     */
    @Async("taskExecutor")
    public void deleteServices(ApplicationServices applicationServices, Boolean enabled,
                               String updateName) {

        Integer serviceId = applicationServices.getId();
        String kongServiceId = applicationServices.getKongServicesName();

        List<ServicesRoutePlugin> servicesRoutePlugins = servicesRoutePluginMapper.selectByServiceIdArrays(RoutesUtil.conventList(serviceId));

        //外层禁用，里面全部禁用
        List<ServicesRoutes> servicesRoutes = servicesRoutesMapper.selectByServiceIdArrays(RoutesUtil.conventList(applicationServices.getId()));

        if (enabled) {//开启
            //查询service详情
            //String serviceName = configs.getSname().replace("@servicename", applicationServices.getApplicationName());
            KongVo.ServicesDetail kongServices = kongHelper.getServices(applicationServices.getKongServicesName());
            if (kongServices == null) {
                //创建services
                KongServiceBo kongServiceBo = KongServiceBo.builder()
                        //.name("service_" + upstream.getName())
                        .name(applicationServices.getKongServicesName())
                        .host(applicationServices.getApplicationName())
                        .build();
                KongServiceBo services = kongHelper.createServices(kongServiceBo);
                applicationServices.setKongServicesId(services.getId());
            }

            //修改网关id的Kong
            applicationServices.setEnabled(true);
            applicationServicesMapper.updateByPrimaryKeySelective(applicationServices);

            //创建kong的限流插件
            if (servicesRoutePlugins != null) {
                for (ServicesRoutePlugin servicesRoutePlugin:servicesRoutePlugins){
                    RateLimitingBo rateLimitingBo = null;
                    if (servicesRoutePlugin != null) {
                        rateLimitingBo = JSONObject.parseObject(servicesRoutePlugin.getPluginConfig(), RateLimitingBo.class);
                    }

                    //创建插件
                    LimitingPluginBo limitingPluginBo = new LimitingPluginBo();
                    limitingPluginBo.setServiceName(applicationServices.getKongServicesName());
                    Map<String, Object> stringObjectMap = null;
                    if (StringUtils.equals(servicesRoutePlugin.getPluginName(), BaseConstants.PluginName.rateLimiting.name())) {
                        //创建插件
                        stringObjectMap = BaseConstants.Period.getPeriods(rateLimitingBo.getPeriod(), rateLimitingBo.getQuantity());
                    } else if (StringUtils.equals(servicesRoutePlugin.getPluginName(), BaseConstants.PluginName.ipRestriction.name())) {
                        stringObjectMap = RoutesUtil.ipRestrictionMaps(rateLimitingBo.getWhitelist(), rateLimitingBo.getBlacklist());
                        limitingPluginBo.setName(BaseConstants.PluginName.ipRestriction.getName());
                    }
                    limitingPluginBo.setConfig(stringObjectMap);
                    LimitingPluginDetailBo limitingPluginDetailBo = kongHelper.createServicesPlugin(limitingPluginBo);
                    if (limitingPluginDetailBo != null) {
                        //修改网关插件的Id
                        rateLimitingBo.setId(limitingPluginDetailBo.getId());
                        servicesRoutePlugin.setPluginConfig(JSONObject.toJSONString(rateLimitingBo));
                        servicesRoutePlugin.setEnabled(true);
                        servicesRoutePluginMapper.updateByPrimaryKeySelective(servicesRoutePlugin);
                    }
                }

            }
            //创建routes
            for (ServicesRoutes servicesRoute : servicesRoutes) {
                deleteRoute(applicationServices, servicesRoute, true, updateName);
            }

        } else {//禁用

            //更改应用层状态
            applicationServices.setEnabled(false);
            applicationServices.setUpdateBy(updateName);
            applicationServices.setKongServicesId("");
            applicationServicesMapper.updateByPrimaryKeySelective(applicationServices);

            //更改插件id
            if (servicesRoutePlugins != null) {
                for (ServicesRoutePlugin servicesRoutePlugin:servicesRoutePlugins){
                    RateLimitingBo rateLimitingBo = null;
                    if (servicesRoutePlugin != null) {
                        rateLimitingBo = JSONObject.parseObject(servicesRoutePlugin.getPluginConfig(), RateLimitingBo.class);
                    }
                    rateLimitingBo.setId(null);
                    servicesRoutePlugin.setPluginConfig(JSONObject.toJSONString(rateLimitingBo));
                    servicesRoutePlugin.setEnabled(false);
                    servicesRoutePluginMapper.updateByPrimaryKeySelective(servicesRoutePlugin);
                }
            }

            /**
             * TODO 先删除routes，才能删除services
             * //先删除routes
             * 删除routes会自动删除下面的插件
             */
            if (servicesRoutes != null) {
                for (ServicesRoutes servicesRoute : servicesRoutes) {
                    deleteRoute(applicationServices, servicesRoute, false, updateName);
                }
            }

            /**
             * //删除services
             * 删除services会自动删除下面的插件
             */
            kongHelper.delServices(kongServiceId);

        }


    }

    /**
     * @param applicationServices 应用
     * @param servicesRoutes      路由
     * @param enabled             操作true/false
     * @desc 禁用-删除应用层
     */
    @Async("taskExecutor")
    public void deleteRoute(ApplicationServices applicationServices, ServicesRoutes servicesRoutes, Boolean enabled, String updateName) {


        if (applicationServices == null) {
            applicationServices = applicationServicesMapper.selectByPrimaryKey(servicesRoutes.getServiceId());
        }

        Integer routeId = servicesRoutes.getId();
        String kongrouteId = servicesRoutes.getKongRoutesName();

        //内部地址
        /*String innerPath = servicesRoutes.getInnerPath();
        if (!innerPath.startsWith("/")) {
            innerPath = "/" + innerPath;
        }*/

        List<ServicesRoutePlugin> servicesRoutePlugins = servicesRoutePluginMapper.selectByRoutesIdArrays(RoutesUtil.conventList(routeId));
        if (enabled) {//开启
            //String routeName = configs.getRname().replace("@routename", applicationServices.getApplicationName()).replace("@type", servicesRoutes.getRouteType());
            //String path = configs.getRpath().replace("@type", servicesRoutes.getRouteType()).replace("@path", applicationServices.getApplicationName());
            //新增新的route
            //4.创建routes
            KongRouteBo routeDetail = kongHelper.getRouteDetail(servicesRoutes.getKongRoutesName());
            if (routeDetail == null) {
                //String path = configs.getRpath().replace("@type", servicesRoutes.getRouteType()).replace("@path", applicationServices.getApplicationName());
                KongRouteBo kongRouteBo = KongRouteBo.builder()
                        .service(KongRouteBo.ServiceBean.builder().id(applicationServices.getKongServicesId()).build())
                        //.name("route_" + upstream.getName() + "_web")
                        .name(servicesRoutes.getKongRoutesName())
                        //.paths(new String[]{"/web/web_" + upstream.getName()})
                        .paths(new String[]{servicesRoutes.getOutPath()})
                        .build();
                KongRouteBo kongRouteBos = kongHelper.createRoutes(kongRouteBo);
                //修改routes的节点kongId
                servicesRoutes.setKongRoutesId(kongRouteBos.getId());
            }

            //修改routes的节点kongId
            servicesRoutes.setEnabled(true);
            servicesRoutesMapper.updateByPrimaryKeySelective(servicesRoutes);


            for (ServicesRoutePlugin servicesRoutePlugin : servicesRoutePlugins) {

                RateLimitingBo rateLimitingBo = JSONObject.parseObject(servicesRoutePlugin.getPluginConfig(), RateLimitingBo.class);
                //创建插件
                LimitingPluginBo limitingPluginBo = new LimitingPluginBo();
                limitingPluginBo.setServiceName(servicesRoutes.getKongRoutesName());
                Map<String, Object> stringObjectMap = null;
                if (StringUtils.equals(servicesRoutePlugin.getPluginName(), BaseConstants.PluginName.rateLimiting.name())) {
                    //创建插件
                    stringObjectMap = BaseConstants.Period.getPeriods(rateLimitingBo.getPeriod(), rateLimitingBo.getQuantity());
                } else if (StringUtils.equals(servicesRoutePlugin.getPluginName(), BaseConstants.PluginName.ipRestriction.name())) {
                    stringObjectMap = RoutesUtil.ipRestrictionMaps(rateLimitingBo.getWhitelist(), rateLimitingBo.getBlacklist());
                    limitingPluginBo.setName(BaseConstants.PluginName.ipRestriction.getName());
                }
                limitingPluginBo.setConfig(stringObjectMap);
                LimitingPluginDetailBo limitingPluginDetailBo = kongHelper.createRoutePlugin(limitingPluginBo);
                if (limitingPluginDetailBo != null) {
                    //修改routes的插件的Id
                    rateLimitingBo.setId(limitingPluginDetailBo.getId());
                    servicesRoutePlugin.setPluginConfig(JSONObject.toJSONString(rateLimitingBo));
                    servicesRoutePlugin.setEnabled(true);
                    servicesRoutePluginMapper.updateByPrimaryKeySelective(servicesRoutePlugin);
                }
            }
        } else {

            //更改路由状态
            servicesRoutes.setEnabled(false);
            servicesRoutes.setUpdateBy(updateName);
            servicesRoutes.setKongRoutesId("");
            servicesRoutesMapper.updateByPrimaryKeySelective(servicesRoutes);

            //禁用插件
            for (ServicesRoutePlugin servicesRoutePlugin : servicesRoutePlugins) {
                RateLimitingBo rateLimitingBo = JSONObject.parseObject(servicesRoutePlugin.getPluginConfig(), RateLimitingBo.class);
                rateLimitingBo.setId(null);
                servicesRoutePlugin.setPluginConfig(JSONObject.toJSONString(rateLimitingBo));
                servicesRoutePlugin.setEnabled(false);
                servicesRoutePluginMapper.updateByPrimaryKeySelective(servicesRoutePlugin);
            }

            kongHelper.delRoutes(kongrouteId);

        }

    }

    /**
     * 发布执行kong的操作
     *
     * @param routePlusBo
     */
    public void deployKong(RoutePlusBo routePlusBo) {

        //1.创建kong的Upstream
        UpstreamBo upstream = kongHelper.getUpstream(routePlusBo.getName());
        if (upstream == null) {
            upstream = kongHelper.createKongUpstream(routePlusBo.getName());
            log.info("【应用：{}】创建Kong的Upstream,结果:{}", routePlusBo.getName(), JSONObject.toJSONString(upstream));
        } else {
            upstream = kongHelper.updateKongUpstream(routePlusBo.getName());
            log.info("【应用：{}】已存在Kong的Upstream", routePlusBo.getName());
        }

        //2.创建kong的Target
        List<RoutePlusBo.ServerBo> serverList = routePlusBo.getServerList();
        KongVo kongBo = kongHelper.getTarget(upstream.getName());
        if (kongBo != null) {
            //删除之前机器的所有占比
            for (TargetBo target : kongBo.getData()) {
                kongHelper.delTarget(upstream.getName(), target.getTarget());
                log.info("【应用：{}】删除Target；target:{},weight:{}", upstream.getName(), target.getTarget(), target.getWeight());
            }

            //如果有新机器，设置新机器占比100%
            for (RoutePlusBo.ServerBo server : serverList) {
                TargetBo targetBo = TargetBo.builder()
                        .upstreamName(upstream.getName())
                        .target(server.getIp() + ":" + server.getPort())
                        .weight(new Long(server.getVagrancy())).build();
                kongHelper.createTarget(targetBo);
                log.info("【应用：{},发布模式:{}】创建Target；target：{},weight：{}", upstream.getName(), routePlusBo.getPublishType(),
                        server.getIp() + ":" + server.getPort(), server.getVagrancy());

            }
            //Map<String,List<Target>> targetsMap=targetsList.stream().collect(org.fibonacci.publish.domain.ListDeployServer(Target::getTarget));
            //Map<String, Target> map = Maps.uniqueIndex(kongBo.getData(), Target::getTarget);
        }
        //services
        this.exeServiceAndRoutes(upstream.getName(), routePlusBo.getGatewayType());
    }

    /**
     * @param upstreamName 应用名称
     * @param gatewayType  路由类型
     */
    @Async("taskExecutor")
    public void exeServiceAndRoutes(String upstreamName, String gatewayType) {
        //处理services
        KongVo.ServicesDetail services = this.createServices(upstreamName, null);
        this.executeRoutes(services.getId(), upstreamName, gatewayType, null, null, true);
    }


    /**
     * 创建services服务
     *
     * @param
     */
    public KongVo.ServicesDetail createServices(String upstreamName, String serviceName) {

        //检查是否有services
        serviceName = (serviceName == null ? configs.getSname().replace("@servicename", upstreamName) : serviceName);
        KongVo.ServicesDetail kongServices = kongHelper.getServices(serviceName);
        if (kongServices == null) {
            //3.创建services
            KongServiceBo kongServiceBo = KongServiceBo.builder()
                    //.name("service_" + upstream.getName())
                    .name(serviceName)
                    .host(upstreamName)
                    .port(8080)
                    .build();
            KongServiceBo kongServiceBos = kongHelper.createServices(kongServiceBo);
            kongServices = new KongVo.ServicesDetail();
            kongServices.setId(kongServiceBos.getId());
            kongServices.setName(kongServiceBos.getName());
            log.info("【应用：{}】创建Kong的Services;处理结果：{}", upstreamName, JSONObject.toJSONString(kongServiceBo));
        }

        return kongServices;

    }

    public List<KongRouteBo> v2CreateRoutes(String kongServiceId, String applicationName, List<String> gatewayTypes, String path) {

        StringBuilder sb = new StringBuilder();
        for (String item : path.split("/")) {
            if (StringUtils.isNotBlank(item)) {
                sb.append("_").append(item);
            }
        }
        String routeName = sb.substring(1).toString();

        List<KongRouteBo> rtn = new ArrayList<>();
        for (String gatewayType : gatewayTypes) {
            KongRouteBo routeBo = KongRouteBo.builder()
                    .service(KongRouteBo.ServiceBean.builder().id(kongServiceId).build())
                    .name(routeName + "_" + gatewayType)
                    .headers(KongRouteBo.GatewayHeader.builder().gateway(Arrays.asList(gatewayType)).build())
                    .paths(new String[]{path})
                    .build();
            KongRouteBo kongRouteBo = kongHelper.createRoutes(routeBo);
            log.info("【应用：{}】创建Kong的Services;处理结果：{}", applicationName, JSONObject.toJSONString(routeBo));
            rtn.add(kongRouteBo);
        }

        return rtn;
    }

    /**
     * 处理路由
     *
     * @param id            kong-id
     * @param upstreamName  应用名
     * @param gatewayType   路由类型
     * @param servicePath   内部路径
     * @param map           业务参数
     * @param hasCheckRoute 是否需要检查route
     */
    private void executeRoutes(String id, String upstreamName, String gatewayType,
                               String servicePath, Map<String, Integer> map, Boolean hasCheckRoute) {

        String[] routeTypes = StringUtils.split(gatewayType, ",");
        List<String> gateway = Arrays.asList(routeTypes);
        String rName = null;
        String path = null;
        if (servicePath != null) {
            rName = routeHelper.getRouteName(servicePath, upstreamName, "");
            path  = routeHelper.getRoutePath(servicePath, upstreamName, "");
        } else {
            rName = configs.getRname().replace("@routename", upstreamName);
            path = configs.getRpath().replace("@path", upstreamName);
        }
        KongRouteBo routeDetail = null;
        if (hasCheckRoute) {//需要检查
            routeDetail = kongHelper.getRouteDetail(rName);
        }

        if (routeDetail == null) {
            //4.创建routes
            KongRouteBo routeBo = KongRouteBo.builder()
                    .service(KongRouteBo.ServiceBean.builder().id(id).build())
                    //.name("route_" + upstream.getName() + "_web")
                    .name(rName)
                    .headers(KongRouteBo.GatewayHeader.builder().gateway(gateway).build())
                    //.paths(new String[]{"/web/web_" + upstream.getName()})
                    .paths(new String[]{"/" + path})
                    .build();
            KongRouteBo kongRouteBo = kongHelper.createRoutes(routeBo);
            log.info("【应用：{}】创建Kong的Services;处理结果：{}", upstreamName, JSONObject.toJSONString(routeBo));

            //更新route业务功能
            for (String routeType : routeTypes) {
                if (map != null) {
                    Integer routesId = map.get(routeType);
                    if (routesId != null && kongRouteBo != null) {
                        ServicesRoutes record = new ServicesRoutes();
                        record.setId(routesId);
                        record.setKongRoutesId(kongRouteBo.getId());
                        record.setKongRoutesName(kongRouteBo.getName());
                        servicesRoutesMapper.updateByPrimaryKeySelective(record);
                    }
                }
            }

        }
    }


    /**
     * 创建services服务
     *
     * @param id           service-id
     * @param upstreamName 应用名称
     * @param gatewayType  路由类型
     * @param servicePath  内部路径
     * @param map          外部参数
     */
    @Async("taskExecutor")
    public void createRoutes(String id, String upstreamName, String gatewayType,
                             String servicePath, Map<String, Integer> map) {

        if (!StringUtils.isBlank(gatewayType)) {
            /*//全部删除之前routes，新增routes
            if (hasOldDel) {
                KongVo.RoutesDetail routes = kongHelper.getRoutes(id);
                if (routes != null && routes.getData() != null) {
                    for (KongVo.RoutesDetail.DataBean dataBean : routes.getData()) {
                        kongHelper.delRoutes(dataBean.getId());
                        log.info("【应用：{}】删除Kong的routes配置", upstreamName);
                        //判断routes下面的插件进行删除
                    }
                }
            }*/
            //处理route
            this.executeRoutes(id, upstreamName, gatewayType, servicePath, map, true);
        }
    }


    /**
     * 发布执行kong的操作
     *
     * @param routePlusBo
     */
    public void removeKong(RoutePlusBo routePlusBo) {

        //删除Kong的upstream
        String name = routePlusBo.getName();
        UpstreamBo upstream = kongHelper.getUpstream(name);
        if (upstream != null) {
            kongHelper.delUpstream(name);
            log.info("【应用：{}】删除Kong的Upstream;", upstream.getName());
        }

        //查询service下面的所有的routes并删除
        String serviceName = configs.getSname().replace("@servicename", name);
        KongVo.RoutesDetail routes = kongHelper.getRoutes(serviceName);
        if (routes != null && routes.getData() != null) {
            for (KongVo.RoutesDetail.DataBean dataBean : routes.getData()) {
                kongHelper.delRoutes(dataBean.getId());
                log.info("【应用：{}】删除Kong的routes配置", name);
            }
        }

        //清空Service配置
        KongVo.ServicesDetail services = kongHelper.getServices(serviceName);
        if (services != null && services.getId() != null) {
            kongHelper.delServices(services.getId());
            log.info("【应用：{}】删除Kong的Service配置", name);
        }


        /*//查询所有route列表
        HttpResult kongRouteslist = HttpHelper.doGet(kongConstants.kongRouteslist, null, false);
        if (kongRouteslist.isSuccess()) {
            KongBo.Routes kongRoutes = JSONObject.parseObject(kongRouteslist.getResponse() + "", KongBo.Routes.class);
            for (KongBo.Routes.DataBean datas : kongRoutes.getData()) {
                HttpHelper.doDelete(kongConstants.kongRoutesDel.replace("{id}", datas.getId()));
                log.warn("【应用：{}】删除Kong的routes名称:{}", upstream.getName(), datas.getName());
            }
        }*/

    }


}
