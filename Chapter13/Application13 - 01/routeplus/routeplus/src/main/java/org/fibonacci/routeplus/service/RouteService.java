package org.fibonacci.routeplus.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.fibonacci.framework.exceptions.ClientException;
import org.fibonacci.routeplus.async.KongAsync;
import org.fibonacci.routeplus.common.bo.RoutePlusBo;
import org.fibonacci.routeplus.common.vo.KongStatusVo;
import org.fibonacci.routeplus.common.vo.KongVo;
import org.fibonacci.routeplus.common.vo.RoutePlusVo;
import org.fibonacci.routeplus.configuration.Configs;
import org.fibonacci.routeplus.constants.BaseConstants;
import org.fibonacci.routeplus.domain.*;
import org.fibonacci.routeplus.helper.KongHelper;
import org.fibonacci.routeplus.helper.RouteHelper;
import org.fibonacci.routeplus.mapper.*;
import org.fibonacci.routeplus.model.RouteVo;
import org.fibonacci.routeplus.model.ServiceVo;
import org.fibonacci.routeplus.model.bo.*;
import org.fibonacci.routeplus.utils.RoutesUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 路由中心
 */
@Slf4j
@Service
public class RouteService {

    @Resource
    private PluginMapper pluginMapper;

    @Resource
    private GatewayPluginMapper gatewayPluginMapper;

    @Resource
    private ApplicationServicesMapper applicationServicesMapper;

    @Resource
    private ServicesRoutesMapper servicesRoutesMapper;

    @Resource
    private ServicesRoutePluginMapper servicesRoutePluginMapper;

    @Resource
    private RouteHelper routeHelper;

    @Resource
    private KongAsync kongAsync;

    @Resource
    private KongHelper kongHelper;

    @Resource
    private Configs configs;


    /**
     * 获取插件列表
     *
     * @return
     */
    public RouteVo listPlugin() {
        List<Plugin> plugins = pluginMapper.listPlugin();
        return RouteVo.builder().plugins(plugins).build();
    }

    /**
     * 父节点路由列表
     *
     * @return
     */
    public RouteVo listRoutes(RouteBo routeBo) {

        PageHelper.startPage(routeBo.currentifPage(), routeBo.sizeif());
        List<GatewayPlugin> gatewayPlugins = new ArrayList<>();
//        List<GatewayPlugin> gatewayPlugins = gatewayPluginMapper.listGatewayPlugin(routeBo.getApplicationId());
        if (gatewayPlugins.size() <= 0) {
            return new RouteVo();
        }

        Map<String, List<GatewayPlugin>> gatewayCollect = gatewayPlugins.stream().collect(Collectors.groupingBy(GatewayPlugin::getName));
        String gateway = BaseConstants.GatewayName.gateway.name();
        String application = BaseConstants.GatewayName.application.name();
        List<GatewayPlugin> gateways = gatewayCollect.get(gateway);
        List<GatewayPlugin> applications = gatewayCollect.get(application);


        List<RouteVo.Plugins> applicationsPluginsList = new ArrayList<>();
        if (applications != null) {
            for (GatewayPlugin gate : applications) {
                RouteVo.Plugins build = this.getRoutePlugins(gate);
                if (build != null) {
                    applicationsPluginsList.add(build);
                }
            }
        }

        List<RouteVo.Gateways> gatewaysList = new ArrayList<>();
        if (gateways != null) {
            Map<String, List<GatewayPlugin>> gatewayTypeCollect = gateways.stream().collect(Collectors.groupingBy(GatewayPlugin::getType));
            if (gatewayTypeCollect != null) {
                for (String type : gatewayTypeCollect.keySet()) {
                    RouteVo.Gateways gatewayRouteVo = new RouteVo.Gateways();
                    List<GatewayPlugin> gatewayPluginsTypes = gatewayTypeCollect.get(type);
                    List<RouteVo.Plugins> gatewaysPluginsList = new ArrayList<>();
                    for (GatewayPlugin gatewayPluginsType : gatewayPluginsTypes) {
                        RouteVo.Plugins build = this.getRoutePlugins(gatewayPluginsType);
                        if (build != null) {
                            gatewaysPluginsList.add(build);
                        }

                    }
                    gatewayRouteVo.setType(type);
                    gatewayRouteVo.setPlugins(gatewaysPluginsList);
                    gatewaysList.add(gatewayRouteVo);
                }
            }
        }
        return RouteVo.builder().applications(RouteVo.Applications.builder()
                .plugins(applicationsPluginsList).build())
                .gateways(gatewaysList).build();
    }


    /**
     * 父节点新增/删除
     *
     * @return
     */
    @Transactional
    public RouteVo saveRoute(RouteBo routeBo) {

        RouteVo.Plugins plugin = routeHelper.getLimitingPlugin(routeBo.getPlugins());
        if (plugin == null) {
            throw new ClientException("10001", "插件类型为空!");
        }
        //List<RouteVo.Plugins> plugins = routeBo.getPlugins();
        //TODO 目前只处理限流插件,支持单条
        GatewayPlugin gatewayPlugin = null;

        if (routeBo.getId() != null) {
            gatewayPlugin = gatewayPluginMapper.selectByPrimaryKey(routeBo.getId());
            if (gatewayPlugin == null) {
                throw new ClientException("10001", "记录不存在!");
            }
        } else {
            gatewayPlugin = gatewayPluginMapper.selectByApplicationId(routeBo.getApplicationId(), routeBo.getType(), plugin.getPluginId());
            if (gatewayPlugin != null) {
                throw new ClientException("10001", "同类型不能重复插入");
            }
        }

        Integer id = null;
        String kongId = null;
        String pluginName = null;

        //动态获取插件的属性
        RateLimitingBo rateLimitingBo = routeHelper.generRateLimiting(plugin);

        if (gatewayPlugin == null) {
            if (plugin.getPluginAttr() == null || plugin.getPluginId() == null) {
                throw new ClientException("10001", "插件参数错误");
            }
            RouteVo.PluginAttr pluginAttr = plugin.getPluginAttr();
            //pluginAttr.setEnabled(plugin.getEnabled());
            //TODO 新增调用kong
            GatewayPlugin build = GatewayPlugin.builder().name(routeBo.getName()).type(routeBo.getType())
                    .applicationId(routeBo.getApplicationId()).applicationName(routeBo.getApplicationName())
                    .pluginId(plugin.getPluginId()).pluginName(plugin.getPluginName())
                    .pluginConfig(JSONObject.toJSONString(pluginAttr)).enabled(plugin.getEnabled()).build();
            gatewayPluginMapper.insertSelective(build);
            id = build.getId();
            pluginName = plugin.getPluginName();


        } else {
            if (gatewayPlugin.getPluginConfig() != null) {
                RateLimitingBo rateLimitingBos = JSONObject.parseObject(gatewayPlugin.getPluginConfig(), RateLimitingBo.class);
                //黑白名单、限流
                rateLimitingBos.setQuantity(rateLimitingBo.getQuantity());
                rateLimitingBos.setPeriod(rateLimitingBo.getPeriod());
                rateLimitingBos.setWhitelist(rateLimitingBo.getWhitelist());
                rateLimitingBos.setBlacklist(rateLimitingBo.getBlacklist());
                gatewayPlugin.setPluginConfig(JSONObject.toJSONString(rateLimitingBos));
                kongId = rateLimitingBos.getId();
            }
            if (plugin.getEnabled() != null) {
                gatewayPlugin.setEnabled(plugin.getEnabled());
            }
            gatewayPluginMapper.updateByPrimaryKeySelective(gatewayPlugin);
            //TODO 修改调用kong
            id = gatewayPlugin.getId();
            routeBo.setApplicationName(gatewayPlugin.getApplicationName());
            routeBo.setType(gatewayPlugin.getType());
            pluginName = gatewayPlugin.getPluginName();
            rateLimitingBo.setEnabled(gatewayPlugin.getEnabled());

        }

        //异步调动kong处理
        PluginAsyncBo build = PluginAsyncBo.builder()
                .id(id)
                .upstreamName(routeBo.getApplicationName())
                .name(routeBo.getName())
                .routeType(routeBo.getType())
                .pluginId(kongId)
                .pluginName(pluginName)
                .rateLimitingBo(rateLimitingBo)
                .build();
        kongAsync.executeGatewayPlugin(build);
        return RouteVo.builder().id(id).build();
    }


    /**
     * 父节点禁用路由
     *
     * @return
     */
    public RouteVo enabledRoute(RouteBo routeBo) {

        List<Integer> ids = routeHelper.combineIds(routeBo);

        for (Integer id : ids) {
            if (id == null) {
                continue;
            }

            GatewayPlugin gatewayPlugin = gatewayPluginMapper.selectByPrimaryKey(id);
            if (gatewayPlugin == null) {
                throw new ClientException("10001", "路由不存在!");
            }

            if (gatewayPlugin.getPluginConfig() == null) {
                throw new ClientException("10001", "插件记录不存在!");
            }

            boolean flag = false;
            if (routeBo.getServices() != null) {
                if (routeBo.getServices().getEnabled() != null) {
                    gatewayPlugin.setEnabled(routeBo.getServices().getEnabled());
                    flag = gatewayPlugin.getEnabled();
                }
            } else {
                if (routeBo.getRoutes() != null) {
                    if (routeBo.getRoutes().getEnabled() != null) {
                        gatewayPlugin.setEnabled(routeBo.getRoutes().getEnabled());
                        flag = gatewayPlugin.getEnabled();
                    }
                }
            }

            RateLimitingBo rateLimitingBo = JSONObject.parseObject(gatewayPlugin.getPluginConfig(), RateLimitingBo.class);

            //record.setPluginConfig(JSONObject.toJSONString(rateLimitingBo));
            gatewayPluginMapper.updateByPrimaryKeySelective(gatewayPlugin);
            //TODO 调用kong
            PluginAsyncBo build = PluginAsyncBo.builder().pluginId(rateLimitingBo.getId())
                    .name(gatewayPlugin.getName())
                    .enabled(flag).build();
            kongAsync.enabledKongPlugin(build);
        }
        return null;
    }


    /**
     * 父节点删除路由
     *
     * @return
     */
    @Transactional
    public RouteVo deleteRoute(RouteBo routeBo) {

        List<Integer> ids = routeHelper.combineIds(routeBo);
        for (Integer id : ids) {
            if (id == null) {
                continue;
            }

            GatewayPlugin gatewayPlugin = gatewayPluginMapper.selectByPrimaryKey(id);
            if (gatewayPlugin == null) {
                throw new ClientException("10001", "路由不存在!");
            }

            if (gatewayPlugin.getPluginConfig() == null) {
                throw new ClientException("10001", "插件记录不存在!");
            }

            if (gatewayPlugin.getEnabled()) {
                throw new ClientException("10001", "请先禁用在关闭!");
            }
            gatewayPluginMapper.deleteByPrimaryKey(id);
            //TODO 调用kong
            RateLimitingBo rateLimitingBo = JSONObject.parseObject(gatewayPlugin.getPluginConfig(), RateLimitingBo.class);
            PluginAsyncBo build = PluginAsyncBo.builder().pluginId(rateLimitingBo.getId())
                    .name(gatewayPlugin.getName()).build();
            kongAsync.deleteKongPlugin(build);
        }
        return null;
    }


    /**
     * 子节点路由列表
     *
     * @return
     */
    public ServiceVo listServices(ServiceBo serviceBo) {

        PageHelper.startPage(serviceBo.currentifPage(), serviceBo.sizeif());
        List<ApplicationServices> applicationServices = applicationServicesMapper.selectByApplicationId(serviceBo.getApplicationId());
        if (applicationServices == null || applicationServices.isEmpty()) {
            return new ServiceVo();
        }
        //查询路由列表
        List<Integer> idLists = applicationServices.stream().map(ApplicationServices::getId).collect(Collectors.toList());
        Map<Integer, List<ServicesRoutes>> idListMap = new HashMap<>();
        if (idLists.size() > 0) {
            List<ServicesRoutes> servicesRoutes = servicesRoutesMapper.selectByServiceIdArrays(idLists);
            if (servicesRoutes.size() > 0) {
                idListMap = servicesRoutes.stream().collect(Collectors.groupingBy(ServicesRoutes::getServiceId));
            }
            //services插件列表(网关插件)
            Map<Integer, List<ServicesRoutePlugin>> serviceidListMap = new HashMap<>();
            List<ServicesRoutePlugin> servicesRoutePlugins = servicesRoutePluginMapper.selectByServiceIdArrays(idLists);
            if (servicesRoutePlugins.size() > 0) {
                serviceidListMap = servicesRoutePlugins.stream().filter(it -> it.getRouteId() == null).collect(Collectors.groupingBy(ServicesRoutePlugin::getServiceId));
            }
            //遍历处理
            for (ApplicationServices applicationService : applicationServices) {
                //应用本身插件
                List<ServicesRoutePlugin> servicesPluginList = serviceidListMap.get(applicationService.getId());
                List<RouteVo.Plugins> pluginsList = routeHelper.routeList(servicesPluginList);
                applicationService.setPlugins(pluginsList);

                //网关列表
                List<ServicesRoutes> servicesRoutesList = idListMap.get(applicationService.getId());
                if (servicesRoutesList != null) {
                    List<ServicesRoutesBo> servicesRoutesBos = new ArrayList<>();
                    for (ServicesRoutes routes : servicesRoutesList) {
                        ServicesRoutesBo build = ServicesRoutesBo.builder().id(routes.getId()).routeType(routes.getRouteType()).build();
                        servicesRoutesBos.add(build);
                    }
                    applicationService.setRoutes(servicesRoutesBos);
                }
            }
        }

        //实例化
        ServiceVo serviceVo = new ServiceVo();
        serviceVo.setServices(applicationServices);
        return serviceVo;
    }

    public ServiceVo v2ListServiceRoutesWithPlugins(ServiceBo serviceBo) {
        PageHelper.startPage(serviceBo.currentifPage(), serviceBo.sizeif());
        ApplicationServices applicationServices = applicationServicesMapper.selectByPrimaryKey(serviceBo.getServiceId());
        if (applicationServices == null) {
            throw new ClientException("100001", "应用不存在!");
        }
        List<ServicesRoutes> servicesRoutes = servicesRoutesMapper.selectByServiceIdArrays(RoutesUtil.conventList(applicationServices.getId()));
        for (ServicesRoutes servicesRoute : servicesRoutes) {
            List<ServicesRoutePlugin> routePlugins = servicesRoutePluginMapper.selectByRoutesIdArrays(Arrays.asList(servicesRoute.getId()));
            servicesRoute.setPlugins(routePlugins.stream().map(it -> {
                RouteVo.Plugins plugins = new RouteVo.Plugins();
                plugins.setEnabled(true);
                plugins.setId(it.getId());
                plugins.setPluginId(it.getPluginId());
                plugins.setPluginName(it.getPluginName());
                return plugins;
            }).collect(Collectors.toList()));
        }

        ServiceVo serviceVo = new ServiceVo();
        serviceVo.setRoutes(servicesRoutes);
        return serviceVo;
    }

    /**
     * 子节点路由列表
     *
     * @return
     */
    public ServiceVo listServicesRoutes(ServiceBo serviceBo) {

        PageHelper.startPage(serviceBo.currentifPage(), serviceBo.sizeif());
        ApplicationServices applicationServices = applicationServicesMapper.selectByPrimaryKey(serviceBo.getServiceId());
        if (applicationServices == null) {
            throw new ClientException("100001", "应用不存在!");
        }

        //Map<Integer, List<ServicesRoutes>> idListMap = new HashMap<>();
        Map<Integer, List<ServicesRoutePlugin>> routeidListMap = new HashMap<>();
        List<ServicesRoutes> servicesRoutes = servicesRoutesMapper.selectByServiceIdArrays(RoutesUtil.conventList(applicationServices.getId()));
        if (servicesRoutes.size() > 0) {
            //idListMap = servicesRoutes.stream().collect(Collectors.groupingBy(ServicesRoutes::getServiceId));
            List<Integer> routeidLists = servicesRoutes.stream().map(ServicesRoutes::getId).collect(Collectors.toList());
            if (routeidLists.size() > 0) {
                //routes插件列表(路由插件)
                List<ServicesRoutePlugin> routePlugins = servicesRoutePluginMapper.selectByRoutesIdArrays(routeidLists);
                if (routePlugins.size() > 0) {
                    routeidListMap = routePlugins.stream().collect(Collectors.groupingBy(ServicesRoutePlugin::getRouteId));
                }
            }
        }


        //遍历处理
        for (ServicesRoutes servicesRoute : servicesRoutes) {
            //网关列表
            List<ServicesRoutePlugin> servicesRoutePlugins = routeidListMap.get(servicesRoute.getId());
            //网关本身插件
            List<RouteVo.Plugins> servicePluginsList = routeHelper.routeList(servicesRoutePlugins);
            servicesRoute.setPlugins(servicePluginsList);
        }

        //实例化
        ServiceVo serviceVo = new ServiceVo();
        serviceVo.setRoutes(servicesRoutes);
        return serviceVo;
    }

    @Transactional
    public ServiceVo v2SaveOrUpdateServices(ServiceBo serviceBo) {

        return new ServiceVo();
    }

    @Transactional
    public ServiceVo v2save(ServiceBo serviceBo) {
        Integer applicationId = serviceBo.getApplicationId();
        String applicationName = serviceBo.getApplicationName();
        String path = serviceBo.getServices().getPath();
        List<ServicesRoutes> servicesRoutes = serviceBo.getServices().getRoutesPool();
        Preconditions.checkArgument(path.startsWith("/" + applicationName), "内部路径错误");

        // create service
        ApplicationServices applicationServices = applicationServicesMapper.selectByApplicationIdAndPath(applicationId, path);
        Preconditions.checkArgument( applicationServices == null, "应用层记录不能重复添加");
        String serviceName = routeHelper.getServiceName(path, serviceBo.getApplicationName());
        KongVo.ServicesDetail createdKongService = kongAsync.createServices(serviceBo.getApplicationName(), serviceName);
        if (createdKongService == null) {
            throw new ClientException("10001", "新增kong的service为空!");
        }
        ApplicationServices insertApplicationServices = ApplicationServices.builder().applicationId(serviceBo.getApplicationId())
                .kongServicesId(createdKongService.getId()).kongServicesName(createdKongService.getName())
                .applicationName(serviceBo.getApplicationName()).path(path)
                .remark(serviceBo.getServices().getRemark()).createBy("system").updateBy("system")
                .build();
        applicationServicesMapper.insertSelective(insertApplicationServices);
        String createdKongServiceId = createdKongService.getId();
        Integer applicationServicesId = insertApplicationServices.getId();


        // create routes if wanna
        List<String> routeTypes = servicesRoutes.stream().map(ServicesRoutes::getRouteType).collect(Collectors.toList());
        if (! CollectionUtils.isEmpty(routeTypes)) {
            List<KongRouteBo> kongRouteBos = kongAsync.v2CreateRoutes(createdKongServiceId, applicationName, routeTypes, path);
            for (KongRouteBo kongRouteBo : kongRouteBos) {
                String gateway = kongRouteBo.getHeaders().getGateway().get(0);
                ServicesRoutes servicesRoutesOld = servicesRoutesMapper.selectByServiceIdAndType(applicationServicesId, gateway);
                if (servicesRoutesOld == null) {
                    ServicesRoutes servicesRoute = ServicesRoutes.builder()
                            .serviceId(applicationServicesId)
                            .routeType(gateway)
                            .innerPath(path)
                            .outPath(path)
                            .kongRoutesId(kongRouteBo.getId())
                            .kongRoutesName(kongRouteBo.getName())
                            .createBy("system")
                            .updateBy("system")
                            .build();
                    servicesRoutesMapper.insertSelective(servicesRoute);
                }
            }
        }
        ServiceVo serviceVo = new ServiceVo();
        serviceVo.setPath(path);
        serviceVo.setRemark(serviceBo.getServices().getRemark());
        serviceVo.setUpdateBy("system");
        serviceVo.setUpdateTime(new Date());
        serviceVo.setGatewayTypes(String.join(",", routeTypes));
        serviceVo.setServiceId(applicationServicesId);
        serviceVo.setApplicationId(serviceBo.getApplicationId());
        serviceVo.setApplicationName(serviceBo.getApplicationName());
        serviceVo.setPlugins(serviceBo.getServices().getPlugins());
        return serviceVo;

    }
    /**
     * 子节点路由新增
     *
     * @return
     */
    @Transactional
    @Deprecated
    public ServiceVo saveOrUpdateServices(ServiceBo serviceBo) {

//        LoginRsp login = BaseConstants.parseJwtTokenLogin();

        Integer id = serviceBo.getServices().getId();
        ApplicationServices build = null;
        //默认应用层是新增，修改时删除网关旧的数据再新增
        Boolean hasDel = false;
        String kongId = null;
        String serviceName = null;
        String path = null;
        Set<String> sourceSet = new HashSet<>();
        Set<String> newSet = new HashSet<>();
        if (id == null) {
            //校验
            path = serviceBo.getServices().getPath();
            if (path == null || path.indexOf("/") == -1) {
                throw new ClientException("10001", "内部路径错误");
            }
            if (serviceBo.getApplicationId() == null) {
                throw new ClientException("10001", "应用id不能为空");
            }
            ApplicationServices applicationServices = applicationServicesMapper.selectByApplicationIdAndPath(serviceBo.getApplicationId(), path);
            if (applicationServices != null) {
                throw new ClientException("10001", "应用层记录不能重复添加");
            }

            //新增kong的services
            serviceName = routeHelper.getServiceName(path, serviceBo.getApplicationName());
            KongVo.ServicesDetail services = kongAsync.createServices(serviceBo.getApplicationName(), serviceName);
            if (services == null) {
                throw new ClientException("10001", "新增kong的service为空!");
            }
            //新增
            build = ApplicationServices.builder().applicationId(serviceBo.getApplicationId())
                    .kongServicesId(services.getId()).kongServicesName(services.getName())
                    .applicationName(serviceBo.getApplicationName()).path(path)
                    .remark(serviceBo.getServices().getRemark()).createBy("system").updateBy("system")
//                    .remark(serviceBo.getServices().getRemark()).createBy(login.getUsername()).updateBy(login.getUsername())
                    .build();
            applicationServicesMapper.insertSelective(build);
            id = build.getId();
            kongId = services.getId();
        } else {
            build = applicationServicesMapper.selectByPrimaryKey(id);
            if (build == null) {
                throw new ClientException("10001", "子节点路由不存在!");
            }
            if (serviceBo.getServices().getPath() != null) {
                throw new ClientException("10001", "子节点路由内部路径不能修改!");
            }

            if (serviceBo.getServices().getRemark() != null) {
                build.setRemark(serviceBo.getServices().getRemark());
            }
//            build.setUpdateBy(login.getUsername());
            build.setUpdateBy("system");
            applicationServicesMapper.updateByPrimaryKeySelective(build);


            //检验
            if (serviceBo.getServices().getRoutesPool() != null) {
                //验证内容，如果没有改变类型，不操作
                List<ServicesRoutes> servicesRoutesList = servicesRoutesMapper.selectByServiceIdArrays(RoutesUtil.conventList(id));
                sourceSet = servicesRoutesList.stream().filter(s -> s != null).map(ServicesRoutes::getRouteType).distinct().collect(Collectors.toSet());
                newSet = serviceBo.getServices().getRoutesPool().stream().filter(s -> s != null).map(ServicesRoutes::getRouteType).distinct().collect(Collectors.toSet());
                if (newSet.containsAll(sourceSet) && newSet.size() == sourceSet.size()) {
                    //如果没有改变
                    return ServiceVo.builder().serviceId(build.getId()).build();
                }

            }
            //标识
            id = build.getId();
            hasDel = true;
            kongId = build.getKongServicesId();
            serviceBo.setApplicationId(build.getApplicationId());
            serviceBo.setApplicationName(build.getApplicationName());
            serviceName = build.getKongServicesName();

        }

        //网关处理
        List<ServicesRoutes> routesPool = serviceBo.getServices().getRoutesPool();
        if (routesPool != null) {
            if (hasDel) {//修改
                //删除取消的路由类型
                List<String> sourceList = new ArrayList<>(sourceSet);
                List<String> newList = new ArrayList<>(newSet);
                sourceList.removeAll(newList);
                //查询
                for (String source : sourceList) {
                    ServicesRoutes servicesRoutesSource = servicesRoutesMapper.selectByServiceIdAndType(id, source);
                    if (servicesRoutesSource != null) {
                        servicesRoutesMapper.deleteByPrimaryKey(servicesRoutesSource.getId());
                        servicesRoutePluginMapper.deleteByRouteId(servicesRoutesSource.getId());
                        if (servicesRoutesSource.getKongRoutesId() != null) {
                            kongHelper.delRoutes(servicesRoutesSource.getKongRoutesId());
                        }
                    }
                }
            }

            //循环处理
            Map<String, Integer> map = new HashMap<>();
            String gatewayTypes = routesPool.stream().map(ServicesRoutes::getRouteType).collect(Collectors.joining(","));
            String servicePath = serviceBo.getServices().getPath() == null ? build.getPath() : serviceBo.getServices().getPath();
            for (ServicesRoutes servicesRoutes : routesPool) {
                //查询应用层下面指定类型的网关类型
                ServicesRoutes servicesRoutesSource = servicesRoutesMapper.selectByServiceIdAndType(id, servicesRoutes.getRouteType());
                if (servicesRoutesSource == null) {
                    //应用记录新增
                    String outPath = routeHelper.getRoutePath(servicePath, build.getApplicationName(), servicesRoutes.getRouteType());
                    ServicesRoutes servicesRoute = ServicesRoutes.builder().serviceId(id)
                            .routeType(servicesRoutes.getRouteType())
                            .innerPath(build.getPath())
                            .outPath(outPath)
                            .remark(servicesRoutes.getRemark())
//                            .createBy(login.getUsername())
//                            .updateBy(login.getUsername())
                            .createBy("system")
                            .updateBy("system")
                            .build();
                    servicesRoutesMapper.insertSelective(servicesRoute);
                    map.put(servicesRoutes.getRouteType(), servicesRoute.getId());
                }
            }

            //新增/修改kong的routes
            kongAsync.createRoutes(kongId, build.getApplicationName(), gatewayTypes, servicePath, map);
        }

        //处理插件
        routeHelper.executeRoutePlugin(id, serviceBo.getServices().getPlugins(), serviceName, true);

        //实例化
        ServiceVo serviceVo = new ServiceVo();
        serviceVo.setPath(path);
        serviceVo.setRemark(serviceBo.getServices().getRemark());
//        serviceVo.setUpdateBy(login.getUsername());
        serviceVo.setUpdateBy("system");
        serviceVo.setUpdateTime(new Date());
        //serviceVo.setGatewayTypes(gatewayTypes);
        serviceVo.setServiceId(id);
        serviceVo.setApplicationId(serviceBo.getApplicationId());
        serviceVo.setApplicationName(serviceBo.getApplicationName());
        serviceVo.setPlugins(serviceBo.getServices().getPlugins());
        return serviceVo;
    }

    /**
     * 子节点网关新增/修改
     *
     * @return
     */
    @Transactional
    public ServiceVo saveOrUpdateServicesRoutes(ServiceBo serviceBo) {
//        LoginRsp login = BaseConstants.parseJwtTokenLogin();
        Integer id = serviceBo.getRoutesPool().getId();
        if (id == null) {
            throw new ClientException("10001", "目前不支持新增");
        }

        ServicesRoutes servicesRoutes = servicesRoutesMapper.selectByPrimaryKey(id);
        if (servicesRoutes == null) {
            throw new ClientException("10001", "网关记录不存在!");
        }

        ApplicationServices applicationServices = applicationServicesMapper.selectByPrimaryKey(servicesRoutes.getServiceId());
        if (applicationServices == null) {
            throw new ClientException("10001", "应用记录不存在!");
        }

        /*if (serviceBo.getRoutesPool().getRouteType() != null) {
            ServicesRoutes servicesRoutesSource = servicesRoutesMapper.selectByServiceIdAndType(serviceBo.getServiceId(), serviceBo.getRoutesPool().getRouteType());
            if (servicesRoutesSource != null) {
                throw new ClientException("10001", "不能添加网关层下相同类型记录");
            }
            servicesRoutes.setRouteType(serviceBo.getRoutesPool().getRouteType());
        }*/
        if (serviceBo.getRoutesPool().getRemark() != null) {
            servicesRoutes.setRemark(serviceBo.getRoutesPool().getRemark());
        }
        if (serviceBo.getRoutesPool().getEnabled() != null) {
            servicesRoutes.setEnabled(serviceBo.getRoutesPool().getEnabled());
        }
        if (serviceBo.getRoutesPool().getOutPath() != null) {
            //验证名称是否存在
            ServicesRoutes servicesRoutesSource = servicesRoutesMapper.selectByServiceIdAndPath(applicationServices.getId(), serviceBo.getRoutesPool().getOutPath());
            if (servicesRoutesSource != null) {
                throw new ClientException("10001", "路径已经存在!");
            }
            servicesRoutes.setOutPath(serviceBo.getRoutesPool().getOutPath());
        }
//        servicesRoutes.setUpdateBy(login.getUsername());
        servicesRoutes.setUpdateBy("system");
        servicesRoutesMapper.updateByPrimaryKeySelective(servicesRoutes);

        //处理应用层插件
        routeHelper.executeRoutePlugin(id, serviceBo.getRoutesPool().getPlugins(), servicesRoutes.getKongRoutesName(), false);

        //TODO 处理kong的routes
        //异步调用kong
        if (serviceBo.getRoutesPool().getOutPath() != null) {
            kongAsync.exeRoutePlugin(serviceBo, servicesRoutes.getKongRoutesName());
        }

        //创建对象
        ServiceVo serviceVo = new ServiceVo();
        serviceVo.setRouteId(id);
        serviceVo.setOutPath(serviceBo.getRoutesPool().getOutPath());
        serviceVo.setRemark(serviceBo.getRoutesPool().getRemark());
        return serviceVo;
    }


    /**
     * 父节点禁用路由
     * 禁用 --
     * 父节点应用层（路由删除，插件禁用）
     * 子节点网官层（路由删除，插件禁用）
     * <p>
     * 启用 --
     * 父节点应用层（路由创建，插件开启）
     * 子节点网官层（路由创建，插件开启）
     *
     * @return
     */
    @Transactional
    public RouteVo enabledService(RouteBo routeBo) {

        RouteBo.RouteInnerService services = routeBo.getServices();
        RouteBo.InnerRoute routes = routeBo.getRoutes();
//        LoginRsp login = BaseConstants.parseJwtTokenLogin();

        if (services != null) {

            Integer id = routeBo.getServices().getId();
            Boolean enabled = routeBo.getServices().getEnabled();
            if (id == null || enabled == null) {
                throw new ClientException("10001", "入参为空!");
            }
            ApplicationServices applicationServices = applicationServicesMapper.selectByPrimaryKey(id);
            if (applicationServices == null) {
                throw new ClientException("10001", "应用记录不存在!");
            }

            //禁用/删除应用层
//            kongAsync.deleteServices(applicationServices, routeBo.getServices().getEnabled(), login.getUsername());
            kongAsync.deleteServices(applicationServices, routeBo.getServices().getEnabled(), "system");
        }

        if (routes != null) {

            if (routeBo.getRoutes().getId() == null || routeBo.getRoutes().getEnabled() == null) {
                throw new ClientException("10001", "入参为空!");
            }
            ServicesRoutes servicesRoutes = servicesRoutesMapper.selectByPrimaryKey(routeBo.getRoutes().getId());
            if (servicesRoutes == null) {
                throw new ClientException("10001", "网关记录不存在!");
            }

            //禁用/删除应用层
            kongAsync.deleteRoute(null, servicesRoutes, routeBo.getRoutes().getEnabled(), "system");
//            kongAsync.deleteRoute(null, servicesRoutes, routeBo.getRoutes().getEnabled(), login.getUsername());
        }
        return null;
    }

    public ServiceVo checkEnabled(Integer serviceId) {

        List<ServicesRoutes> servicesRoutes = servicesRoutesMapper.selectByServiceIdArrays(RoutesUtil.conventList(serviceId));
        List<Boolean> collect = servicesRoutes.stream().filter(s -> s != null).map(ServicesRoutes::getEnabled).collect(Collectors.toList());
        ServiceVo serviceVo = new ServiceVo();
        serviceVo.setHasEnabled(collect.contains(true));
        return serviceVo;
    }


    /**
     * 父节点删除路由
     *
     * @return
     */
    @Transactional
    public RouteVo deleteService(RouteBo routeBo) {

        RouteBo.RouteInnerService services = routeBo.getServices();
        RouteBo.InnerRoute routes = routeBo.getRoutes();

        if (services != null) {
            Integer id = routeBo.getServices().getId();
            if (id == null) {
                throw new ClientException("10001", "入参为空!");
            }
            ApplicationServices applicationServices = applicationServicesMapper.selectByPrimaryKey(id);
            if (applicationServices == null) {
                throw new ClientException("10001", "应用记录不存在!");
            }
            if (applicationServices.getEnabled()) {
                throw new ClientException("10001", "请先禁用再删除!");
            }

            applicationServicesMapper.deleteByPrimaryKey(id);
            List<ServicesRoutePlugin> servicesRoutePlugins = servicesRoutePluginMapper.selectByServiceIdArrays(RoutesUtil.conventList(id));
            for (ServicesRoutePlugin servicesRoutePlugin : servicesRoutePlugins) {
                //RateLimitingBo rateLimitingBo = JSONObject.parseObject(servicesRoutePlugin.getPluginConfig(), RateLimitingBo.class);
                    /*PluginAsyncBo build = PluginAsyncBo.builder().pluginId(rateLimitingBo.getId())
                            .name(BaseConstants.GatewayName.application.name()).build();
                    kongAsync.deleteKongPlugin(build);*/
                servicesRoutePluginMapper.deleteByPrimaryKey(servicesRoutePlugin.getId());

            }

            //删除操作
            List<ServicesRoutes> servicesRoutesList = servicesRoutesMapper.selectByServiceIdArrays(RoutesUtil.conventList(applicationServices.getId()));
            if (servicesRoutesList != null) {
                for (ServicesRoutes servicesRoutes : servicesRoutesList) {
                    servicesRoutesMapper.deleteByPrimaryKey(servicesRoutes.getId());
                    servicesRoutePluginMapper.deleteByRouteId(servicesRoutes.getId());
                }
            }

        }

        if (routes != null) {

            Integer id = routeBo.getRoutes().getId();
            if (id == null) {
                throw new ClientException("10001", "入参为空!");
            }
            ServicesRoutes servicesRoutes = servicesRoutesMapper.selectByPrimaryKey(id);
            if (servicesRoutes == null) {
                throw new ClientException("10001", "网关记录不存在!");
            }
            if (servicesRoutes.getEnabled()) {
                throw new ClientException("10001", "请先禁用再删除!");
            }

            List<ServicesRoutePlugin> servicesRoutePlugins = servicesRoutePluginMapper.selectByRoutesIdArrays(RoutesUtil.conventList(id));
            for (ServicesRoutePlugin servicesRoutePlugin : servicesRoutePlugins) {
                //TODO 调用kong
                //RateLimitingBo rateLimitingBo = JSONObject.parseObject(servicesRoutePlugin.getPluginConfig(), RateLimitingBo.class);
                    /*PluginAsyncBo build = PluginAsyncBo.builder().pluginId(rateLimitingBo.getId())
                            .name(BaseConstants.GatewayName.gateway.name()).build();
                    kongAsync.deleteKongPlugin(build);*/
                servicesRoutePluginMapper.deleteByPrimaryKey(servicesRoutePlugin.getId());
            }
            servicesRoutesMapper.deleteByPrimaryKey(id);
        }
        return null;
    }

    /**
     * 父节点删除路由
     *
     * @return
     */
    public RouteVo deletePlugin(Integer id, String name) {

        //插件业务id
        if (id == null) {
            throw new ClientException("10001", "入参为空!");
        }
        ServicesRoutePlugin servicesRoutePlugin = servicesRoutePluginMapper.selectById(id);
        if (servicesRoutePlugin == null) {
            throw new ClientException("10001", "插件记录不存在!");
        }
        if (servicesRoutePlugin.getEnabled()) {
            throw new ClientException("10001", "请先禁用再删除!");
        }

        RateLimitingBo rateLimitingBo = JSONObject.parseObject(servicesRoutePlugin.getPluginConfig(), RateLimitingBo.class);
        if (!StringUtils.isBlank(rateLimitingBo.getId())) {
            //删除kong的插件
            PluginAsyncBo build = PluginAsyncBo.builder()
                    .name(name)
                    .pluginId(rateLimitingBo.getId())
                    .build();
            kongAsync.deleteKongPlugin(build);
        }

        servicesRoutePluginMapper.deleteByPrimaryKey(id);
        return null;
    }

    /**
     * @return
     */
    public RoutePlusVo deployKong(RoutePlusBo routePlusBo) {

        kongAsync.deployKong(routePlusBo);
        return new RoutePlusVo();

    }

    /**
     * @return
     */
    public RoutePlusVo removeKong(RoutePlusBo routePlusBo) {

        kongAsync.removeKong(routePlusBo);
        return new RoutePlusVo();

    }

    /**
     * @return
     */
    public KongVo getTarget(RoutePlusBo routePlusBo) {
        return kongHelper.getTarget(routePlusBo.getName());
    }

    /**
     * @return
     */
    public KongStatusVo checkStatus() {
        KongStatusVo kongStatusVo = kongHelper.checkKongStatus();
        if (kongStatusVo == null) {
            kongStatusVo = new KongStatusVo();
        }
        kongStatusVo.setNode(configs.getNode());
        return kongStatusVo;
    }

    /**
     * @return
     */
    public KongStatusVo updateRoute(String upstreamName, String gatewayType) {
        kongAsync.exeServiceAndRoutes(upstreamName, gatewayType);
        return new KongStatusVo();
    }

    /**
     * 获取路由对象
     *
     * @param gate
     * @return
     */
    private RouteVo.Plugins getRoutePlugins(GatewayPlugin gate) {

        if (gate == null) {
            return null;
        }
        RouteVo.Plugins build = RouteVo.Plugins.builder().id(gate.getId())
                .pluginId(gate.getPluginId()).pluginName(gate.getPluginName()).build();
        if (!StringUtils.isBlank(gate.getPluginConfig())) {
            RateLimitingBo rateLimitingBo = JSONObject.parseObject(gate.getPluginConfig(), RateLimitingBo.class);
            build.setPluginAttr(RouteVo.PluginAttr.builder()
                    .quantity(rateLimitingBo.getQuantity()).period(rateLimitingBo.getPeriod())
                    .blacklist(rateLimitingBo.getBlacklist()).whitelist(rateLimitingBo.getWhitelist())
                    .build());
            build.setEnabled(gate.getEnabled());
        }
        return build;
    }
}

