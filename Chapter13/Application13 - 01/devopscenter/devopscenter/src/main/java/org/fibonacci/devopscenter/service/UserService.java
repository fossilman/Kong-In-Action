package org.fibonacci.devopscenter.service;

import lombok.extern.slf4j.Slf4j;
import org.fibonacci.devopscenter.domain.Login;
import org.fibonacci.routeplus.common.vo.KongStatusVo;
import org.fibonacci.routeplus.rpc.feign.RouteFeignClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @description: 检测Kong状态
 * @author: zachary
 * @date: 2020-06-01 16:50
 */
@Slf4j
@Service
public class UserService {


    @Resource
    private RouteFeignClient routeFeignClient;

    /**
     * 检查kong状态
     *
     * @return
     */
    public Login checkKong() {
        return this.checkKongStatus();
    }


    /**
     * @description: 调用检测kong
     * @param
     * @return {@link Login}
     * @author: zachary
     * @date: 2020-06-01 16:50
     */
    public Login checkKongStatus() {
        //实例化
        Login login = new Login();
        //调用feign服务
        KongStatusVo kongStatusVo = routeFeignClient.checkStatus();
        if (kongStatusVo == null || kongStatusVo.getDatabase() == null) {
            login.setKongStatus(Boolean.FALSE);
        } else {
            login.setKongStatus(kongStatusVo.getDatabase().isReachable());
        }
        login.setNode(kongStatusVo.getNode());
        return login;
    }

}
