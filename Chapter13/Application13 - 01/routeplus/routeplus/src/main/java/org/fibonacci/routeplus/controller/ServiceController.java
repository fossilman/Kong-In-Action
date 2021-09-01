package org.fibonacci.routeplus.controller;

import org.fibonacci.framework.exceptions.ClientException;
import org.fibonacci.framework.exceptions.ServerException;
import org.fibonacci.framework.util.ResultCode;
import org.fibonacci.routeplus.model.RouteVo;
import org.fibonacci.routeplus.model.ServiceVo;
import org.fibonacci.routeplus.model.bo.RouteBo;
import org.fibonacci.routeplus.model.bo.ServiceBo;
import org.fibonacci.routeplus.service.RouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @desc 路由中心
 */
@RestController
@RequestMapping("/services")
@Slf4j
public class ServiceController {

    @Resource
    private RouteService routeService;

    /**
     * 子节点应用列表
     *
     * @return
     */
    @PostMapping("/list")
    public ResultCode<ServiceVo> listServices(@RequestBody @Validated(ServiceBo.queryService.class) ServiceBo serviceBo) {
        ServiceVo serviceVo = null;
        try {
            serviceVo = routeService.listServices(serviceBo);
        } catch (ServerException | ClientException be) {
            log.error("子节点应用列表业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            log.error("子节点应用列表系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, "子节点应用列表成功", serviceVo);
    }

    /**
     * 子节点网关列表
     *
     * @return
     */
    @PostMapping("/routes/list")
    public ResultCode<ServiceVo> listServicesRoutes(@RequestBody @Validated(ServiceBo.queryServiceRoutes.class) ServiceBo serviceBo) {
        ServiceVo serviceVo = null;
        try {
//            serviceVo = routeService.listServicesRoutes(serviceBo);
            serviceVo = routeService.v2ListServiceRoutesWithPlugins(serviceBo);
        } catch (ServerException | ClientException be) {
            log.error("子节点网关列表业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            log.error("子节点网关列表系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, "子节点网关列表成功", serviceVo);
    }




    /**
     * 子节点应用新增/保存
     *
     * @return
     */
    @PostMapping("/saveOrUpdate")
    public ResultCode<ServiceVo> saveOrUpdateServices(@RequestBody @Validated(ServiceBo.saveService.class) ServiceBo serviceBo) {
        ServiceVo serviceVo = null;
        try {
//            serviceVo = routeService.saveOrUpdateServices(serviceBo);
            serviceVo = routeService.v2save(serviceBo);
        } catch (ServerException | ClientException be) {
            log.error("子节点新增/修改业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            log.error("子节点新增/修改系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, "子节点新增/修改成功", serviceVo);
    }

    /**
     * 子节点网关新增/保存
     *
     * @return
     */
    @PostMapping("/routes/saveOrUpdate")
    public ResultCode<ServiceVo> saveOrUpdateServicesRoutes(@RequestBody @Validated(ServiceBo.saveServiceRoute.class) ServiceBo serviceBo) {
        ServiceVo serviceVo = null;
        try {
            serviceVo = routeService.saveOrUpdateServicesRoutes(serviceBo);
        } catch (ServerException | ClientException be) {
            log.error("子节点网关新增/修改业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            log.error("子节点网关新增/修改系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, "子节点网关新增/修改成功", serviceVo);
    }

    /**
     * 子节点禁用
     *
     * @return
     */
    @GetMapping("/routes/checkEnabled")
    public ResultCode<ServiceVo> checkEnabled(@RequestParam Integer id) {
        ServiceVo serviceVo = null;
        try {
            serviceVo = routeService.checkEnabled(id);
        } catch (ServerException | ClientException be) {
            log.error("判断是否存在开启业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            log.error("判断是否存在开启系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, "判断是否存在开启成功", serviceVo);
    }

    /**
     * 子节点禁用
     *
     * @return
     */
    @PostMapping("/enabled")
    public ResultCode<RouteVo> enabledService(@RequestBody @Validated(RouteBo.enabledRoute.class) RouteBo routeBo) {
        RouteVo gitLabVo = null;
        try {
            gitLabVo = routeService.enabledService(routeBo);
        } catch (ServerException | ClientException be) {
            log.error("子节点禁用业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            log.error("子节点禁用系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, "子节点禁用成功", gitLabVo);
    }

    /**
     * 子节点删除
     *
     * @return
     */
    @PostMapping("/delete")
    public ResultCode<RouteVo> deleteService(@RequestBody @Validated(RouteBo.deleteRoute.class) RouteBo routeBo) {
        RouteVo gitLabVo = null;
        try {
            gitLabVo = routeService.deleteService(routeBo);
        } catch (ServerException | ClientException be) {
            log.error("子节点删除业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            log.error("子节点删除系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, "子节点删除成功", gitLabVo);
    }

    /**
     * 子节点插件删除
     *
     * @return
     */
    @GetMapping("/plugin/delete")
    public ResultCode<RouteVo> deletePlugin(@RequestParam Integer id, @RequestParam String name) {
        RouteVo gitLabVo = null;
        try {
            gitLabVo = routeService.deletePlugin(id,name);
        } catch (ServerException | ClientException be) {
            log.error("子节点删除业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            log.error("子节点删除系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, "子节点删除成功", gitLabVo);
    }
}
