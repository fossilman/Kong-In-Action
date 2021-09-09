package org.fibonacci.devopscenter.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.fibonacci.devopscenter.async.PublishAsync;
import org.fibonacci.devopscenter.constants.PublishConstants;
import org.fibonacci.devopscenter.constants.ShellConstants;
import org.fibonacci.devopscenter.domain.ListBuild;
import org.fibonacci.devopscenter.domain.ListDeploy;
import org.fibonacci.devopscenter.domain.ListDeployServer;
import org.fibonacci.devopscenter.domain.PublishList;
import org.fibonacci.devopscenter.helper.DeployAbstract;
import org.fibonacci.devopscenter.helper.JenkinsHelper;
import org.fibonacci.devopscenter.helper.LoginHelper;
import org.fibonacci.devopscenter.helper.ShellHelper;
import org.fibonacci.devopscenter.mapper.ListBuildMapper;
import org.fibonacci.devopscenter.mapper.ListDeployMapper;
import org.fibonacci.devopscenter.mapper.ListDeployServerMapper;
import org.fibonacci.devopscenter.mapper.PublishListMapper;
import org.fibonacci.framework.exceptions.ClientException;
import org.fibonacci.framework.exceptions.ServerException;
import org.fibonacci.framework.global.AppInfo;
import org.fibonacci.framework.httpclient.HttpClientTemplate;
import org.fibonacci.framework.util.ResultCode;
//import edu.jiahui.inneruser.response.LoginRsp;
//import edu.jiahui.routeplus.common.bo.RoutePlusBo;
//import edu.jiahui.routeplus.common.bo.TargetBo;
//import edu.jiahui.routeplus.common.vo.KongVo;
//import edu.jiahui.routeplus.rpc.feign.RouteFeignClient;
import lombok.extern.slf4j.Slf4j;
import model.PublishVo;
import model.bo.HarBorBo;
import model.bo.PublishBo;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * Copyright (C) 2020 Shanghai LuoJin Com., Ltd. All rights reserved.
 * <p>
 * No parts of this file may be reproduced or transmitted in any form or by any means,
 * electronic, mechanical, photocopying, recording, or otherwise, without prior written
 * permission of Shanghai LuoJin Com., Ltd.
 *
 * @author krame
 * @date 2020/11/26
 */
@Slf4j
@Service
public class PublishService {

    @Resource
    private GitLabService gitLabService;

    @Resource
    private JenkinsHelper jenkinsHelper;

    /**
     * 发布服务层
     */
    @Resource
    private PublishListMapper publishListMapper;


    @Resource
    private PublishAsync publishAsync;


    @Resource
    private ListBuildMapper listBuildMapper;

    @Autowired
    private AppInfo appInfo;

    @Resource
    private LoginHelper loginHelper;

    @Resource
    private ListDeployMapper listDeployMapper;

    @Resource
    private ListDeployServerMapper listDeployServerMapper;

    @Resource
    private DeployAbstract deployAbstract;

    //@Resource
    //private RouteFeignClient routeFeignClient;

    @Resource
    private ShellConstants shellConstants;

    @Resource
    private ShellHelper shellHelper;

    @Resource
    private HttpClientTemplate httptemplate;


    /**
     * 获取发布列表
     *
     * @param publishBo
     * @return
     */
    public PublishVo list(PublishBo publishBo) {

        //LoginRsp login = loginHelper.parseJwtTokenLogin();
        //if (login.getGroups() == null) {
        //    throw new ServerException("10003", "用户组为空！");
        //}
        //List<String> groupName = login.getGroups().stream().map(LoginRsp.Group::getGroupName).collect(Collectors.toList());
        //publishBo.setGroupNameList(groupName);
        PageHelper.startPage(publishBo.currentifPage(), publishBo.sizeif());
        List<PublishList> publishListList = publishListMapper.listByPublish(publishBo);
        List<Long> idList = publishListList.stream().filter(x -> publishListList != null).map(PublishList::getId).collect(Collectors.toList());
        if (idList.size() > 0) {
            //查询服务器列表
            List<ListDeploy> listDeploys = listDeployMapper.selectBylistIdArray(idList);
            Map<Long, List<ListDeploy>> longListMap = listDeploys.stream().filter(x -> listDeploys != null).collect(Collectors.groupingBy(ListDeploy::getListId));

            //查询编译列表
            List<ListBuild> listBuilds = listBuildMapper.selectBylistIdArrays(idList);
            Map<Long, List<ListBuild>> listBuildMap = listBuilds.stream().filter(x -> listBuilds != null).collect(Collectors.groupingBy(ListBuild::getListId));

            publishListList.forEach(publish -> {
                List<ListDeploy> listDeployrs = longListMap.get(publish.getId());
                List<ListBuild> listListBuild = listBuildMap.get(publish.getId());
                publish.setListDeploys(listDeployrs);
                publish.setListBuilds(listListBuild);
            });

        }

        PageInfo<PublishList> pageInfo = new PageInfo<PublishList>(publishListList);
        PublishVo publishVo = new PublishVo();
        publishVo.setPublishList(publishListList);
        publishVo.setTotal(Integer.parseInt(pageInfo.getTotal() + ""));
        return publishVo;
    }

    /**
     * 获取发布列表
     *
     * @param id
     * @return
     */
    public PublishVo detail(Long id) {
        PublishList publishList = publishListMapper.selectByPrimaryKey(id);
        PublishVo PublishVo = new PublishVo();
        if (publishList != null) {
            PublishVo.setGatewayType(publishList.getRouteType());
            PublishVo.setName(publishList.getName());
            PublishVo.setType(publishList.getType());
        }
        return PublishVo;
    }

    /**
     * 保存发布
     *
     * @param publishBo
     * @return
     */
    @Transactional
    public PublishVo  save(PublishBo publishBo) {

        Integer gitlabId = gitLabService.getGitLabInfo(publishBo.getApplicationName());
        if (gitlabId == null) {
            throw new ServerException("10003", "获取gitlabid异常!");
        }

        //LoginRsp login = loginHelper.parseJwtTokenLogin();
        //if (login.getGroups() != null) {
        //    String groupName = login.getGroups().stream().map(LoginRsp.Group::getGroupName).collect(Collectors.joining(","));
        //    publishBo.setGroupName(groupName);
        //}

        //新增
        PublishList record = new PublishList();
        record.setName(publishBo.getApplicationName());
        record.setNum(publishBo.getServerList() != null ? publishBo.getServerList().size() : null);
        record.setRemark(publishBo.getRemark());
        record.setGitlabId(gitlabId);
        record.setType(publishBo.getType());
        record.setKind(publishBo.getKind());
        record.setRouteType(publishBo.getGatewayType());
        record.setGroups(publishBo.getGroupName());
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        //record.setCreateBy(login.getUsername());
        publishListMapper.insertSelective(record);

        //构建返回状态
        PublishVo publishVo = new PublishVo();
        publishVo.setId(record.getId());
        return publishVo;
    }


    /**
     * 更新发布记录
     *
     * @param publishBo
     * @return
     */
    @Transactional
    public PublishVo update(PublishBo publishBo) {
        PublishList publishList = publishListMapper.selectByPrimaryKey(publishBo.getId());
        if (publishList == null) {
            throw new ClientException(1001 + "", "发布记录不存在");
        }
        if (StringUtils.isBlank(publishBo.getGatewayType())) {
            throw new ClientException(1001 + "", "发布策略为空");
        }
        if (!StringUtils.isBlank(publishList.getRouteType())) {
            List<String> routeType = Arrays.asList(StringUtils.split(publishList.getRouteType(), ","));
            String gateway = publishBo.getGatewayType();
            routeType.forEach(s -> {
                if (gateway.indexOf(s) == -1) {
                    throw new ClientException(1001 + "", "修改策略错误!");
                }
            });

        }

        PublishList record = new PublishList();
        record.setId(publishBo.getId());
        record.setRouteType(publishBo.getGatewayType());
        //record.setName(publishBo.getApplicationName());
        //record.setRemark(publishBo.getRemark());
        //record.setNum(publishBo.getServerList() == null ? null : publishBo.getServerList().size());
        record.setUpdateTime(new Date());
        //record.setType(publishBo.getType());
        //更新发布记录
        publishListMapper.updateByPrimaryKeySelective(record);
        //更新路由策略
        //publishAsync.updateRoute(publishList.getName(), publishList.getRouteType());
        return null;
    }

    /**
     * 删除发布记录
     *
     * @param id
     */

    @Transactional
    public void del(Integer id) {


        int size = publishListMapper.deleteByPrimaryKey(new Long(id));
        if (size == 0) {
            throw new ServerException("10003", "先停用才能删除！");
        }

        //删除发布记录
        List<Long> listDeploys = listDeployMapper.selectByDeployId(new Long(id));
        if (!CollectionUtils.isEmpty(listDeploys)) {
            listDeployServerMapper.deleteByDeployId(listDeploys);
        }
        listDeployMapper.deleteByListid(new Long(id));

        //删除编译记录
        listBuildMapper.deleteByListid(new Long(id));

    }

    /**
     * 停用/启用
     *
     * @param id
     */
    public void stop(Integer id, Integer status) {

        PublishList publishsource = publishListMapper.selectByPrimaryKey(new Long(id));
        if (publishsource == null) {
            throw new ServerException("10003", "发布应用记录不存在！");
        }


        PublishList publishList = new PublishList();
        publishList.setId(new Long(id));
        publishList.setDel(status);
        publishList.setUpdateTime(new Date());
        int size = publishListMapper.updateByPrimaryKeySelective(publishList);
        if (size == 0) {
            throw new ServerException("10003", "记录不存在！");
        }

        //停止应用
        ListDeploy listDeploys = listDeployMapper.selectBylistidforLaster(publishsource.getId());
        if (listDeploys != null) {
            ListBuild listBuilds = listBuildMapper.selectByPrimaryKey(new Long(listDeploys.getBuildId()));
            if (listBuilds != null) {
                List<ListDeployServer> beforeIpList = listDeployServerMapper.selectByDeployid(listDeploys.getId());
                if (beforeIpList.size() > 0) {
                    String stopShellParam = beforeIpList.stream().map(ListDeployServer::getServerIp).distinct().collect(Collectors.joining(" "));
                    String stopShellUrl = "ssh -p 10022 root@" + shellConstants.shellFileIp + " sh " + shellConstants.stopUrl + " " + listBuilds.getRemark() + " " + stopShellParam;
                    log.warn("调用dev_stop:shell脚本命令：【" + stopShellUrl + "】");
                    long start = System.currentTimeMillis();
                    int stopCode = shellHelper.executeShellReturnexitValue(stopShellUrl);
                    log.warn("调用dev_stop耗时：{},脚本结果返回：{}", (System.currentTimeMillis() - start) + "/ms", stopCode);
                }
            }
        }

        //kong的配置下线，后端外部项目，删除Kong配置
        if (PublishConstants.APPLICATION_STATUS.server_outer.name().equals(publishsource.getType())) {
            publishAsync.removeKong(publishsource.getName());
        }

    }

    /**
     * 构建发布
     *
     * @param publishBo
     * @return
     * @throws IOException
     */
    public PublishVo build(PublishBo publishBo) throws IOException, URISyntaxException {
        log.warn("构建build入参：" + JSONObject.toJSONString(publishBo));
        //获取用户信息
        //LoginRsp login = loginHelper.parseJwtTokenLogin();

        PublishList publishList = publishListMapper.selectByPrimaryKey(publishBo.getId());
        if (publishList == null) {
            throw new ServerException("10003", "发布信息不存在!");
        }

        //check build status
        String os = appInfo.getEnv();

        String osProjects = publishList.getName().concat("_") + os;
//        String osProjects = publishList.getName().concat("_") + "dev";
        ListBuild listBuild = listBuildMapper.selectByListidAndVersion(publishBo.getId(), publishBo.getGitlabVersion());
        if (listBuild != null && StringUtils.equals(listBuild.getBuildStatus(), PublishConstants.BUILD_STATUS.ING.name())) {
            throw new ServerException("10003", osProjects + ":镜像构建中，请不要重复构建!");
        }

        //项目发布名称 替换成 jenkins名称
        //拼接调用harbor地址
//        String harborNo = UUID.randomUUID().toString();
        String harborNo = publishBo.getGitlabVersion();
        String harborAddr = jenkinsHelper.harborShellUrl.replace("@project", publishList.getName()).
                replace("@env", os).replace("@harbokey", harborNo);
        Map<String, Object> params = new HashMap<>();
//        params.put("TAG", harborNo);
        params.put("TAG", publishBo.getGitlabVersion());
        params.put("NAME", publishList.getName());
        params.put("ENV", os);
        Integer jobId = jenkinsHelper.build(osProjects, params);

        //2.新增构建记录
        ListBuild record = new ListBuild();
        record.setListId(publishBo.getId());
        record.setGitlabId(publishList.getGitlabId());
        record.setGitlabVersion(publishBo.getGitlabVersion());
        record.setGitlabHead(publishBo.getGitlabHead());
        record.setGitlabDesc(publishBo.getGitlabDesc());
        record.setPushAuthor(publishBo.getPushAuthor());
        record.setGitlabTime(new Date());
        //job
        record.setJenkinsId(jobId);
        record.setHarborKey(harborNo);
        record.setBuildStatus(PublishConstants.BUILD_STATUS.ING.name());
        //record.setCreateBy(login.getUsername());
        //record.setUpdateBy(login.getUsername());
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        record.setRemark(harborAddr);
        listBuildMapper.insertSelective(record);

        //构建对象
        PublishVo publishVo = new PublishVo();
        publishVo.setId(record.getId());
        publishVo.setHarborNo(harborNo);
        publishVo.setBuildStatus("ING");
        return publishVo;
    }


    /**
     * 构建发布
     *
     * @param publishBo
     * @return
     */
    public PublishVo buildDetail(PublishBo publishBo) {

        PageHelper.startPage(publishBo.currentifPage(), publishBo.sizeif());
        List<ListBuild> listBuilds = listBuildMapper.selectByListid(publishBo.getBuildId());

        PageInfo<ListBuild> pageInfo = new PageInfo<ListBuild>(listBuilds);
        PublishVo publishVo = new PublishVo();
        publishVo.setBuildList(listBuilds);
        publishVo.setTotal(Integer.parseInt(pageInfo.getTotal() + ""));
        //查询编译列表
        return publishVo;
    }


    /**
     * 检查构建状态
     */
    public PublishVo checkBuild(Long id) {

        ListBuild listBuild = listBuildMapper.selectByPrimaryKey(id);
        if (listBuild == null) {
            throw new ServerException("10003", "构建信息不存在!");
        }
        PublishList publishList = publishListMapper.selectByPrimaryKey(listBuild.getListId());
        if (publishList == null) {
            throw new ServerException("10003", "发布信息不存在!");
        }


        String os = appInfo.getEnv();

        String osProjects = publishList.getName().concat("_") + os;
//        String osProjects = publishList.getName().concat("_") + "dev";

        /**
         * 检查jenkins运行状态
         * 1.如果jenkis失败，返回失败，并记录原因
         * 2.如果harbor失败，返回失败，并记录原因
         * 3.jenkins、harbor都成功，返回成功
         */
        PublishVo publishVo = new PublishVo();
        String failReason = null;
        ResultCode<String> finished = jenkinsHelper.checkBuild(osProjects, listBuild.getJenkinsId());
        String result = finished.getRetval() + "";
        if (!finished.isSuccess()) {//重试ING
            //更新失败原因
            ListBuild record = new ListBuild();
            record.setId(id);
            record.setFailReason(finished.getErrmsg());
            record.setUpdateTime(new Date());
            record.setBuildStatus(result);
            listBuildMapper.updateByPrimaryKeySelective(record);
            publishVo.setStatus(result);
            return publishVo;
        }

        log.warn("【编译项目：{},jobid:{},调用Jekins获取结果:{}】", osProjects, listBuild.getJenkinsId(), result);

        //调用harbor
        String status = PublishConstants.BUILD_STATUS.ING.name();
        String harborUrl = StringUtils.replace(jenkinsHelper.harborUrl, "@project", publishList.getName()).
                replace("@env", os).replace("@harbokey", listBuild.getHarborKey());
        log.info("harborUrl {}", harborUrl);
        try {
            //harbor parma combo
            String name = jenkinsHelper.harborUser;
            String password = jenkinsHelper.harborPassword;
            String authString = name + ":" + password;
            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes("utf-8"));
            String authStringEnc = new String(authEncBytes);
            Map<String, String> head = new HashMap<>();
            head.put("Authorization", "Basic " + authStringEnc);
            String harborResult = httptemplate.doGet(harborUrl, null, head);
            log.warn("【编译项目：{},harborUrl:{},调用Harbor获取结果:{}】", osProjects, harborUrl, harborResult);
            HarBorBo harBorBos = JSONObject.parseObject(harborResult, HarBorBo.class);
            if (harBorBos != null && harBorBos.getName() != null) {
                status = PublishConstants.BUILD_STATUS.SUCCESS.name();
            } else {
                status = PublishConstants.BUILD_STATUS.FAIL.name();
                failReason = "harbor镜像不存在,url=" + harborUrl;
            }
        } catch (Exception e) {
            if (e.getMessage().indexOf("404") == -1) {
                log.error("调用harbor返回数据错误！", e);
                status = PublishConstants.BUILD_STATUS.FAIL.name();
                failReason = "调用harbor异常：msg" + e.getMessage() + ",harbor地址：" + harborUrl;
            } else {
                //harbor镜像还没上传成功，持续获取
                //更新harbor失败原因
                ListBuild record = new ListBuild();
                record.setId(id);
                record.setFailReason("fail msg:" + e.getMessage());
                record.setUpdateTime(new Date());
                listBuildMapper.updateByPrimaryKeySelective(record);
            }

        }

        if (!StringUtils.equals(status, PublishConstants.BUILD_STATUS.ING.name())) {
            //更新构建状态
            ListBuild record = new ListBuild();
            record.setId(id);
            record.setBuildStatus(status);
            record.setFailReason(failReason);
            record.setUpdateTime(new Date());
            listBuildMapper.updateByPrimaryKeySelective(record);
        }
        //RETURN
        publishVo.setStatus(status);
        return publishVo;
    }

    /**
     * 发布
     */
    @Transactional
    public PublishVo deploy(PublishBo publishBo) {
        log.warn("发布deploy入参：" + JSONObject.toJSONString(publishBo));

        //init check
        PublishList publishList = deployAbstract.initCheckDeploy(publishBo);

        //处理发布流程
        publishBo.setName(publishList.getName());
        publishBo.setListId(publishList.getId());
        Long deployid = deployAbstract.executeDeploy(publishBo);


        //异步调用KONG处理逻辑,后台外部项目才会操作KONG
        if (PublishConstants.APPLICATION_STATUS.server_outer.name().equals(publishList.getType())) {
            publishBo.setGatewayType(publishList.getRouteType());
            publishAsync.executeKong(publishBo);
            log.warn("调用routeplus创建kong网关 {}", JSON.toJSONString(publishBo));
        }

        //构建对象
        PublishVo publishVo = new PublishVo();
        publishVo.setDeployId(deployid);
        return publishVo;
    }


    /**
     * 预发布
     */
    public PublishVo deployLog(PublishBo publishBo) {

        //init check
        PublishList publishList = deployAbstract.initCheckDeploy(publishBo);

        //处理发布流程
        publishBo.setName(publishList.getName());
        publishBo.setListId(publishList.getId());
        publishBo.setRemark(publishList.getRemark());
        String result = deployAbstract.executeDeployLog(publishBo);

        //构建对象
        PublishVo publishVo = new PublishVo();
        publishVo.setRemark(result);
        return publishVo;
    }

    /**
     * 发布检查
     */
    @Transactional
    public PublishVo checkDeploy(Long deployId, Boolean isjob) {
        log.info("发布检查，传入deployId：" + deployId);

        //处理发布流程
        PublishBo publishBo = new PublishBo();
        publishBo.setDeployId(deployId);
        publishBo.setIsJob(isjob);
        String deployStatus = deployAbstract.checkDeploy(publishBo);

        //构建对象
        PublishVo publishVo = new PublishVo();
        publishVo.setDeployStatus(deployStatus);
        return publishVo;
    }

    /**
     * 发布详情
     */
    public PublishVo deployDetail(PublishBo publishBo) {

        PageHelper.startPage(publishBo.currentifPage(), publishBo.sizeif());
        List<ListDeploy> listDeploys = listDeployMapper.selectBylistid(publishBo.getPublishId(), null);

        PageInfo<ListDeploy> pageInfo = new PageInfo<ListDeploy>(listDeploys);
        PublishVo publishVo = new PublishVo();
        publishVo.setListDeploys(listDeploys);
        publishVo.setTotal(Integer.parseInt(pageInfo.getTotal() + ""));

        //查询服务器列表
        return publishVo;
    }

    /**
     * 发布详情-子页面
     */
    public PublishVo deployDetailPage1(Long deployId) {

        ListDeploy listDeploy = listDeployMapper.selectById(deployId);
        if (listDeploy == null) {
            throw new ServerException("10003", "详情记录不存在！");
        }

        PublishVo publishVo = new PublishVo();
        List<ListDeployServer> newDeployServerList = listDeployServerMapper.selectByVersion(deployId, listDeploy.getGitlabVersion());
        List<ListDeployServer> oldDeployServerList = listDeployServerMapper.selectByVersion(deployId, listDeploy.getBeforeGitlabVersion());

        //查询详情
        ListBuild listBuild = listBuildMapper.selectByHarborKey(listDeploy.getGitlabVersion());
        ListBuild oldlistBuild = listBuildMapper.selectByHarborKey(listDeploy.getBeforeGitlabVersion());
        if (listBuild != null) {
            publishVo.setRemark(listBuild.getGitlabDesc());
        }
        if (oldlistBuild != null) {
            publishVo.setRemark1(oldlistBuild.getGitlabDesc());
        }

        //获取Kong所有的网关列表
        //RoutePlusBo routePlusBo = new RoutePlusBo();
        //routePlusBo.setName(listDeploy.getListName());
        //KongVo kongVo = routeFeignClient.getTarget(routePlusBo);
        //if (kongVo != null) {
        //    List<TargetBo> data = kongVo.getData();
        //    publishVo.setTargets(data);
        //}
        publishVo.setNewDeployServerList(newDeployServerList);
        publishVo.setOldDeployServerList(oldDeployServerList);
        return publishVo;

    }
}
