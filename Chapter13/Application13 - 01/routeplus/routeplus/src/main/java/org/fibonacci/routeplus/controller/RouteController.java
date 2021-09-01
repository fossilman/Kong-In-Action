package org.fibonacci.routeplus.controller;

import org.fibonacci.framework.exceptions.ClientException;
import org.fibonacci.framework.exceptions.ServerException;
import org.fibonacci.framework.util.ResultCode;
import org.fibonacci.routeplus.model.RouteVo;
import org.fibonacci.routeplus.model.bo.RouteBo;
import org.fibonacci.routeplus.service.RouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @desc 路由中心
 */
@RestController
@RequestMapping("/routes")
@Slf4j
public class RouteController {


    /**
     * 路由服务
     */
    @Resource
    private RouteService routeService;

    /**
     * 插件列表
     *
     * @return
     */
    @GetMapping("/plugin")
    public ResultCode<RouteVo> listPlugin() {

        RouteVo gitLabVo = null;
        try {
            gitLabVo = routeService.listPlugin();
        } catch (ServerException | ClientException be) {
            log.error("插件列表业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            log.error("插件列表系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, "插件列表成功", gitLabVo);
    }


    /**
     * 父节点应用列表
     *
     * @return
     */
    @PostMapping("/list")
    public ResultCode<RouteVo> listRoutes(@RequestBody @Validated(RouteBo.queryRoute.class) RouteBo routeBo) {
        RouteVo gitLabVo = null;
        try {
            gitLabVo = routeService.listRoutes(routeBo);
        } catch (ServerException | ClientException be) {
            log.error("父节点应用列表业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            log.error("父节点应用列表系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, "父节点应用列表成功", gitLabVo);
    }

    /**
     * 父节点新增/修改
     *
     * @return
     */
    @PostMapping("/saveOrUpdate")
    public ResultCode<RouteVo> saveRoute(@RequestBody @Validated(RouteBo.saveRoute.class) RouteBo routeBo) {
        RouteVo gitLabVo = null;
        try {
            gitLabVo = routeService.saveRoute(routeBo);
        } catch (ServerException | ClientException be) {
            log.error("父节点新增/修改业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            log.error("父节点新增/修改系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, "父节点新增/修改成功", gitLabVo);
    }

    /**
     * 父节点禁用
     *
     * @return
     */
    @PostMapping("/enabled")
    public ResultCode<RouteVo> enabledRoute(@RequestBody @Validated(RouteBo.enabledRoute.class) RouteBo routeBo) {
        RouteVo gitLabVo = null;
        try {
            gitLabVo = routeService.enabledRoute(routeBo);
        } catch (ServerException | ClientException be) {
            log.error("父节点禁用业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            log.error("父节点禁用系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, "父节点禁用成功", gitLabVo);
    }

    /**
     * 父节点删除
     *
     * @return
     */
    @PostMapping("/delete")
    public ResultCode<RouteVo> deleteRoute(@RequestBody @Validated(RouteBo.deleteRoute.class) RouteBo routeBo) {
        RouteVo gitLabVo = null;
        try {
            gitLabVo = routeService.deleteRoute(routeBo);
        } catch (ServerException | ClientException be) {
            log.error("父节点删除业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            log.error("父节点删除系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, "父节点删除成功", gitLabVo);
    }


}
