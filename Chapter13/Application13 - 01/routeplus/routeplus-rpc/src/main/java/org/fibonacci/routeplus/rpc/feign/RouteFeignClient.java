package org.fibonacci.routeplus.rpc.feign;

import org.fibonacci.routeplus.common.bo.RoutePlusBo;
import org.fibonacci.routeplus.common.vo.KongStatusVo;
import org.fibonacci.routeplus.common.vo.KongVo;
import org.fibonacci.routeplus.common.vo.RoutePlusVo;
import org.fibonacci.routeplus.constant.ServiceConstant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * feign服务
 */
@FeignClient(name = ServiceConstant.SERVICE_NAME)
@RequestMapping(ServiceConstant.ROOT_URL)
public interface RouteFeignClient {

    /**
     * 发布调用kong
     *
     * @param routePlusBo
     * @return
     */
    @PostMapping("/routes/deploy/kong")
    RoutePlusVo deployKong(@RequestBody @Validated(RoutePlusBo.deployKong.class) RoutePlusBo routePlusBo);

    /**
     * 删除调用kong
     *
     * @param routePlusBo
     * @return
     */
    @PostMapping("/routes/remove/kong")
    RoutePlusVo removeKong(@RequestBody @Validated(RoutePlusBo.removeKong.class) RoutePlusBo routePlusBo);

    /**
     * 获取Kong所有的网关列表
     *
     * @return
     */

    @PostMapping("/routes/get/target")
    KongVo getTarget(@RequestBody @Validated(RoutePlusBo.getTargetKong.class) RoutePlusBo routePlusBo);

    /**
     * 获取Kong所有的网关列表
     *
     * @return
     */
    @GetMapping("/routes/check/status")
    KongStatusVo checkStatus();

    /**
     * 更新路由
     *
     * @return
     */
    @GetMapping("/routes/update/route")
    KongStatusVo updateRoute(@RequestParam String upstreamName, @RequestParam String gatewayType);

}
