package org.fibonacci.devopscenter.controller;

import lombok.extern.slf4j.Slf4j;
import model.ServerVo;
import model.bo.PublishBo;
import model.bo.ServerBo;
import org.fibonacci.devopscenter.async.PublishAsync;
import org.fibonacci.devopscenter.service.ServerService;
import org.fibonacci.framework.exceptions.ServerException;
import org.fibonacci.framework.util.ResultCode;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Copyright (C) 2020 Shanghai LuoJin Com., Ltd. All rights reserved.
 * <p>
 * No parts of this file may be reproduced or transmitted in any form or by any means,
 * electronic, mechanical, photocopying, recording, or otherwise, without prior written
 * permission of Shanghai LuoJin Com., Ltd.
 *
 * @author krame
 * @desc 服务器配置维护
 * @date 2020/11/26
 */
@RestController
@RequestMapping("/server")
@Slf4j
public class ServerController {


    /**
     * 服务器访问
     */
    @Resource
    private ServerService serverService;

    @Resource
    private PublishAsync publishAsync;


    /**
     * 服务器列表
     *
     * @param serverBo
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/list")
    public ResultCode<ServerVo> list(@RequestBody ServerBo serverBo) {

        ServerVo serverVo = null;
        try {
            serverVo = serverService.list(serverBo);
        } catch (ServerException be) {
            log.error("服务器列表业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            log.error("服务器列表系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, null, serverVo);
    }


    /**
     * 服务器保存
     *
     * @param serverBo
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    public ResultCode<ServerVo> save(@RequestBody @Validated(ServerBo.saveServerGroup.class) ServerBo serverBo) {

        ServerVo serverVo = null;
        try {
            serverVo = serverService.save(serverBo);
        } catch (ServerException be) {
            log.error("服务器保存业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
                return ResultCode.getFailure(null, "不能添加重复ip记录");
            }
            log.error("服务器保存系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, null, serverVo);

    }

    /**
     * 服务器修改
     *
     * @param serverBo
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/update")
    public ResultCode<ServerVo> update(@RequestBody @Validated(ServerBo.updateServerGroup.class) ServerBo serverBo) {

        ServerVo serverVo = null;
        try {
            serverVo = serverService.update(serverBo);
        } catch (ServerException be) {
            log.error("服务器修改业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
                return ResultCode.getFailure(null, "ip记录已存在！");
            }
            log.error("服务器修改系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, null, serverVo);
    }

    /**
     * 服务器删除
     *
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/del/{id}")
    public ResultCode<String> del(@PathVariable("id") Integer id) {

        ServerVo serverVo = null;
        try {
            serverVo = serverService.del(id);
        } catch (ServerException be) {
            log.error("服务器删除业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            log.error("服务器删除系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.SUCCESS;
    }

    /**
     * 服务器剩余列表
     *
     * @param team
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/surplus/list/{listid}/{team}")
    public ResultCode<ServerVo> surplusList(@PathVariable("listid") Long listid, @PathVariable("team") String team) {

        ServerVo serverVo = null;
        try {
            serverVo = serverService.surplusList(listid, team);
        } catch (ServerException be) {
            log.error("服务器修改业务异常", be);
            return ResultCode.getFailure(be.getCode(), be.getMessage());
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
                return ResultCode.getFailure(null, "ip记录已存在！");
            }
            log.error("服务器修改系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, null, serverVo);
    }


    /**
     * 服务器剩余列表
     *
     * @param
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/test1")
    public ResultCode<ServerVo> test1(@RequestParam String name) {

        ServerVo serverVo = null;
        try {
            PublishBo publishBo = new PublishBo();
            publishBo.setName(name);
            List<ServerBo> serverList = new ArrayList<>();

            ServerBo serverBo = new ServerBo();
            serverBo.setVagrancy(100);
            serverBo.setPort("8080");
            serverBo.setIp("172.19.22.6");
            serverList.add(serverBo);
            publishBo.setServerList(serverList);
            publishBo.setPublishType("SIMPLE");
            publishBo.setGatewayType("gc");
            publishAsync.executeKong(publishBo);

        } catch (ServerException be) {
            log.error("服务器修改业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
                return ResultCode.getFailure(null, "ip记录已存在！");
            }
            log.error("服务器修改系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, null, serverVo);
    }

    /*public static void main(String[] args) {

        String plainCredentials = "1041ca72a3cd46fb806dda9d577e1c84:5d6ac9fa5d3a45ee9414aa3747b84cb3";
// 填入 plainCredentials（使用 Base64 算法编码的 Credential），计算 base64Credentials，即你要的 Authorization 字段。
        String base64Credentials = new String(Base64.encodeBase64(plainCredentials.getBytes()));
        System.out.println(base64Credentials);
    }*/
}
