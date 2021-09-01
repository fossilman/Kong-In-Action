package org.fibonacci.devopscenter.controller;


import javax.annotation.Resource;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import model.bo.LoginBo;
import org.fibonacci.devopscenter.domain.Login;
import org.fibonacci.devopscenter.service.UserService;
import org.fibonacci.framework.exceptions.ServerException;
import org.fibonacci.framework.global.AppInfo;
import org.fibonacci.framework.util.ResultCode;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

//import org.fibonacci.inneruser.request.LoginReq;
//import org.fibonacci.rpc.inneruser.InnerUserFeignClient;

/**
 * <p>
 * Copyright (C) 2020 Shanghai LuoJin Com., Ltd. All rights reserved.
 * <p>
 * No parts of this file may be reproduced or transmitted in any form or by any means,
 * electronic, mechanical, photocopying, recording, or otherwise, without prior written
 * permission of Shanghai LuoJin Com., Ltd.
 *
 * @author krame
 * @desc 用户
 * @date 2020/11/26
 */
@RestController
@Slf4j
public class UserController {

    /**
     * 登录
     */
    @Resource
    private UserService userService;

    //@Autowired
    //private InnerUserFeignClient innerUserFeignClient;

    @Resource
    private AppInfo appInfo;


    @RequestMapping(method = RequestMethod.POST, value = "/user/login")
    public ResultCode<Login> login(@RequestBody @Valid LoginBo loginBo) {
        //LoginReq loginReq = new LoginReq();
        //loginReq.setUsername(loginBo.getName());
        //loginReq.setPassword(loginBo.getPassword());
        //ResultCode<String> loginCode = null;
        Login login = null;
        try {
//            ResultCode<String> loginCode = innerUserFeignClient.login(appInfo.getAppName(), loginReq);
//            ResultCode<String> loginCode = innerUserFeignClient.login("publish", loginReq);
//            if (!loginCode.isSuccess()) {
//                return ResultCode.getFailure(loginCode.getErrcode(), loginCode.getErrmsg());
//            }
//            login = new Login();
//            login.setToken(loginCode.getRetval() + "");
        } catch (ServerException be) {
            log.error("登录业务异常", be);
            return ResultCode.getFailure(be.getCode() + "", be.getMessage());
        } catch (Exception e) {
            log.error("登录系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, null, login);

    }


    /***
     * 检测Kong状态
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/check/kong")
    public ResultCode<Login> checkKong() {

        Login login = null;
        try {
            login = userService.checkKong();
        } catch (ServerException be) {
            log.error("检测Kong状态业务异常", be);
            return ResultCode.getFailure(be.getCode() + "", be.getMessage());
        } catch (Exception e) {
            log.error("检测Kong状态系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, null, login);

    }


}
