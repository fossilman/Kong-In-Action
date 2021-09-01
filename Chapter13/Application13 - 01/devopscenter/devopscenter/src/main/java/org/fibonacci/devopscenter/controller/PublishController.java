package org.fibonacci.devopscenter.controller;


import org.fibonacci.devopscenter.service.GitLabService;
import org.fibonacci.devopscenter.service.PublishService;
import org.fibonacci.framework.exceptions.ClientException;
import org.fibonacci.framework.exceptions.ServerException;
import org.fibonacci.framework.util.ResultCode;
import lombok.extern.slf4j.Slf4j;
import model.GitLabVo;
import model.PublishVo;
import model.bo.PublishBo;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * Copyright (C) 2020 Shanghai LuoJin Com., Ltd. All rights reserved.
 * <p>
 * No parts of this file may be reproduced or transmitted in any form or by any means,
 * electronic, mechanical, photocopying, recording, or otherwise, without prior written
 * permission of Shanghai LuoJin Com., Ltd.
 *
 * @author krame
 * @desc 发布相关
 * @date 2020/11/26
 */
@RestController
@RequestMapping("/projects")
@Slf4j
public class PublishController {

    /**
     * gitlab服务
     */
    @Resource
    private GitLabService gitLabService;

    /**
     * 发布服务
     */
    @Resource
    private PublishService publishService;


    /**
     * 获取应用的版本号列表
     *
     * @param applicationId-应用id
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/version/{applicationId}")
    public ResultCode<GitLabVo> projectsInfo(@PathVariable("applicationId") Integer applicationId) {

        GitLabVo gitLabVo = null;
        try {
            gitLabVo = gitLabService.projectsInfo(applicationId);
        } catch (ServerException | ClientException be) {
            log.error("获取gitlab应用信息业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            log.error("获取gitlab应用信息系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, "获取应用版本号列表成功", gitLabVo);
    }


    /**
     * 获取发布的列表
     *
     * @param publishBo
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/list")
    public ResultCode<PublishVo> list(@RequestBody PublishBo publishBo) {

        PublishVo publishVo = null;
        try {
            publishVo = publishService.list(publishBo);
        } catch (ServerException | ClientException be) {
            log.error("修改发布业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            log.error("修改发布系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, "获取发布列表成功", publishVo);
    }

    /**
     * 获取发布列表的详情
     *
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/detail/{id}")
    public ResultCode<PublishVo> detail(@PathVariable("id") Long id) {

        PublishVo publishVo = null;
        try {
            publishVo = publishService.detail(id);
        } catch (ServerException | ClientException be) {
            log.error("发布详情业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            log.error("发布详情系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, "发布详情成功", publishVo);
    }


    /**
     * 保存发布
     *
     * @param publishBo
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    public ResultCode<PublishVo> save(@RequestBody @Validated(PublishBo.savePublishGroup.class) PublishBo publishBo) {
        PublishVo publishVo = null;
        try {
            publishVo = publishService.save(publishBo);
        } catch (ServerException | ClientException be) {
            log.error("保存发布业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
                return ResultCode.getFailure(null, "不能添加重复应用名称");
            }
            log.error("保存发布系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, "保存发布成功", publishVo);
    }


    /**
     * 修改发布
     *
     * @param publishBo
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/update")
    public ResultCode<PublishVo> update(@RequestBody @Validated(PublishBo.updatePublishGroup.class) PublishBo publishBo) {
        PublishVo publishVo = null;
        try {
            publishVo = publishService.update(publishBo);
        } catch (ServerException | ClientException be) {
            log.error("修改发布业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            log.error("修改发布系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, "修改发布成功", publishVo);
    }

    /**
     * 删除发布
     *
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/del/{id}")
    public ResultCode<String> del(@PathVariable("id") Integer id) {

        try {
            publishService.del(id);
        } catch (ServerException | ClientException be) {
            log.error("删除发布业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            log.error("删除发布系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.SUCCESS;

    }

    /**
     * 停用/启用
     *
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/stop/{id}/{status}")
    public ResultCode<String> stop(@PathVariable("id") Integer id, @PathVariable("status") Integer status) {

        try {
            publishService.stop(id, status);
        } catch (ServerException | ClientException be) {
            log.error("删除发布业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            log.error("删除发布系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.SUCCESS;

    }

    /**
     * 执行应用编译
     *
     * @param publishBo
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/build")
    public ResultCode<PublishVo> build(@RequestBody @Validated(PublishBo.buildPublishGroup.class) PublishBo publishBo) {
        PublishVo publishVo = null;
        try {
            publishVo = publishService.build(publishBo);
        } catch (ServerException | ClientException be) {
            log.error("build应用业务异常", be);
            return ResultCode.getFailure(be.getCode() + "", be.getMessage());
        } catch (Exception e) {
            log.error("build应用系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, "build应用成功", publishVo);
    }

    /**
     * build详情
     *
     * @param publishBo
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/build/detail")
    public ResultCode<PublishVo> buildDetail(@RequestBody PublishBo publishBo) {
        PublishVo publishVo = null;
        try {
            publishVo = publishService.buildDetail(publishBo);
        } catch (ServerException | ClientException be) {
            log.error("build详情业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            log.error("build详情系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, "build详情成功", publishVo);

    }


    /**
     * 检查jenkins执行状态
     *
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/build/check/{id}")
    public ResultCode<PublishVo> checkBuild(@PathVariable("id") Long id) {
        PublishVo publishVo = null;
        try {
            publishVo = publishService.checkBuild(id);
        } catch (ServerException | ClientException be) {
            log.error("检查发布业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            log.error("检查发布系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, "查询jenkins状态成功", publishVo);
    }


    /**
     * 发布
     *
     * @param publishBo
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/publish")
    public ResultCode<PublishVo> publish(@RequestBody @Validated(PublishBo.deployPublishGroup.class) PublishBo publishBo) {
        PublishVo publishVo = null;
        try {
            publishVo = publishService.deploy(publishBo);
        } catch (ServerException | ClientException be) {
            log.error("发布镜像业务异常", be);
            return ResultCode.getFailure(be.getCode() + "", be.getMessage());
        } catch (Exception e) {
            log.error("发布镜像系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, "发布成功", publishVo);

    }

    /**
     * 预发布
     *
     * @param publishBo
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/log")
    public ResultCode<PublishVo> publishLog(@RequestBody @Validated(PublishBo.deployPublishGroup.class) PublishBo publishBo) {
        PublishVo publishVo = null;
        try {
            publishVo = publishService.deployLog(publishBo);
        } catch (ServerException | ClientException be) {
            log.error("预发布镜像业务异常", be);
            return ResultCode.getFailure(be.getCode() + "", be.getMessage());
        } catch (Exception e) {
            log.error("预发布镜像系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, "预发布成功", publishVo);

    }

    /**
     * 发布状态检查
     *
     * @param deployId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/publish/check/{deployId}")
    public ResultCode<PublishVo> publishCheck(@PathVariable("deployId") Long deployId) {
        PublishVo publishVo = null;
        try {
            publishVo = publishService.checkDeploy(deployId,false);
        } catch (ServerException | ClientException be) {
            log.error("检查发布镜像业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            log.error("检查发布镜像系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, "检查发布成功", publishVo);

    }

    /**
     * deploy详情列表
     *
     * @param publishBo
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/publish/detail")
    public ResultCode<PublishVo> publishDetail(@RequestBody PublishBo publishBo) {
        PublishVo publishVo = null;
        try {
            publishVo = publishService.deployDetail(publishBo);
        } catch (ServerException | ClientException be) {
            log.error("发布详情业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            log.error("发布详情系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, "发布详情成功", publishVo);

    }

    /**
     * deploy详情-子页面列表
     *
     * @param deployId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/publish/detail/page/{deployId}")
    public ResultCode<PublishVo> publishDetailPage1(@PathVariable("deployId") Long deployId) {
        PublishVo publishVo = null;
        try {
            publishVo = publishService.deployDetailPage1(deployId);
        } catch (ServerException | ClientException be) {
            log.error("发布详情子页面查询业务异常", be);
            return ResultCode.getFailure(null, be.getMessage());
        } catch (Exception e) {
            log.error("发布详情子页面查询系统异常", e);
            return ResultCode.getFailure(null, "服务器繁忙，请稍后重试！");
        }
        return ResultCode.getSuccessReturn(null, "发布详情子页面查询成功", publishVo);

    }


}
