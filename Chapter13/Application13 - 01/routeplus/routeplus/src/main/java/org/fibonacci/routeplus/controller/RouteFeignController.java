package org.fibonacci.routeplus.controller;

import org.fibonacci.routeplus.common.bo.RoutePlusBo;
import org.fibonacci.routeplus.common.vo.KongStatusVo;
import org.fibonacci.routeplus.common.vo.KongVo;
import org.fibonacci.routeplus.common.vo.RoutePlusVo;
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
public class RouteFeignController {

    @Resource
    private RouteService routeService;

    /**
     * 发布调用kong处理
     * @return
     */
    @PostMapping("/deploy/kong")
    public RoutePlusVo deployKong(@RequestBody @Validated(RoutePlusBo.deployKong.class) RoutePlusBo routePlusBo) {
        return routeService.deployKong(routePlusBo);
    }

    /**
     * 删除调用kong处理
     * @return
     */
    @PostMapping("/remove/kong")
    public RoutePlusVo removeKong(@RequestBody @Validated(RoutePlusBo.removeKong.class) RoutePlusBo routePlusBo) {
        return routeService.removeKong(routePlusBo);
    }

    /**
     * 获取Kong所有的网关列表
     * @return
     */
    @PostMapping("/get/target")
    public KongVo getTarget(@RequestBody @Validated(RoutePlusBo.getTargetKong.class) RoutePlusBo routePlusBo) {
        return routeService.getTarget(routePlusBo);
    }

    /**
     * 获取Kong所有的网关列表
     * @return
     */
    @GetMapping("/check/status")
    public KongStatusVo checkStatus() {
        return routeService.checkStatus();
    }

    /**
     * 更新路由
     * @return
     */
    @GetMapping("/update/route")
    public KongStatusVo updateRoute(@RequestParam String upstreamName, @RequestParam String gatewayType) {
        return routeService.updateRoute(upstreamName,gatewayType);
    }

}
