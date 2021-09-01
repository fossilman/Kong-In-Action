package org.fibonacci.devopscenter.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.fibonacci.devopscenter.domain.ListBuild;
import org.fibonacci.devopscenter.domain.ListDeployServer;
import org.fibonacci.devopscenter.domain.PublishList;
import org.fibonacci.devopscenter.domain.Server;
import org.fibonacci.devopscenter.helper.JenkinsHelper;
import org.fibonacci.devopscenter.mapper.ListBuildMapper;
import org.fibonacci.devopscenter.mapper.ListDeployServerMapper;
import org.fibonacci.devopscenter.mapper.PublishListMapper;
import org.fibonacci.devopscenter.mapper.ServerMapper;
import org.fibonacci.framework.exceptions.ServerException;
import org.fibonacci.framework.global.AppInfo;
import org.fibonacci.framework.httpclient.HttpClientTemplate;
import lombok.extern.slf4j.Slf4j;
import model.ServerVo;
import model.bo.HarBorBo;
import model.bo.ServerBo;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class ServerService {


    @Resource
    private ServerMapper serverMapper;

    @Resource
    private PublishListMapper publishListMapper;

    @Resource
    private ListDeployServerMapper listDeployServerMapper;

    @Resource
    private JenkinsHelper jenkinsHelper;


    @Resource
    private ListBuildMapper listBuildMapper;

    @Resource
    private HttpClientTemplate httptemplate;

    @Autowired
    private AppInfo appInfo;

    /**
     * 获取发布列表
     *
     * @param serverBo
     * @return
     */
    public ServerVo list(ServerBo serverBo) {

        PageHelper.startPage(serverBo.currentifPage(), serverBo.sizeif());
        List<Server> serverList = serverMapper.listByServer(serverBo);
        PageInfo<Server> pageInfo = new PageInfo<Server>(serverList);

        ServerVo serverVo = new ServerVo();
        //List<Server> serverLists = new ArrayList<>();
        serverVo.setServerList(serverList);
        serverVo.setTotal(Integer.parseInt(pageInfo.getTotal() + ""));
        return serverVo;
    }


    /**
     * 保存
     *
     * @param serverBo
     * @return
     */
    public ServerVo save(ServerBo serverBo) {

        Server server = new Server();
        server.setIp(serverBo.getIp());
        server.setName(serverBo.getName());
        server.setRemark(serverBo.getRemark());
        server.setPort(serverBo.getPort());
        server.setTeam(serverBo.getTeam());
        server.setCreateTime(new Date());
        server.setUpdateTime(new Date());
        int size = serverMapper.insertSelective(server);
        if (size == 0) {
            throw new ServerException("10003", "保存失败!");
        }
        //构建对象
        ServerVo serverVo = new ServerVo();
        serverVo.setId(server.getId());
        return serverVo;
    }


    /**
     * 修改
     *
     * @param serverBo
     * @return
     */
    public ServerVo update(ServerBo serverBo) {

        Server server = new Server();
        server.setId(serverBo.getId());
        server.setIp(serverBo.getIp());
        server.setPort(serverBo.getPort());
        server.setName(serverBo.getName());
        server.setTeam(serverBo.getTeam());
        server.setRemark(serverBo.getRemark());
        server.setUpdateTime(new Date());
        int size = serverMapper.updateByPrimaryKeySelective(server);
        if (size == 0) {
            throw new ServerException("10003", "记录不存在,修改失败!");
        }
        //构建对象
        ServerVo serverVo = new ServerVo();
        serverVo.setId(server.getId());
        return serverVo;

    }


    /**
     * 删除
     *
     * @param id
     * @return
     */
    public ServerVo del(Integer id) {

        int size = serverMapper.deleteByPrimaryKey(new Long(id));
        if (size == 0) {
            throw new ServerException("10003", "记录不存在，删除失败！");
        }
        return new ServerVo();
    }


    /**
     * 剩余服务器列表
     *
     * @param team
     * @return
     */
    public ServerVo surplusList(Long listid, String team) throws UnsupportedEncodingException {

        PublishList publishList = publishListMapper.selectByPrimaryKey(listid);
        if (publishList == null) {
            throw new ServerException("10003", "发布应用记录不存在!");
        }

        //获取harbor的列表显示
        ServerVo serverVo = new ServerVo();
        String os = appInfo.getEnv();

        //harbor parma combo
        String name = jenkinsHelper.harborUser;
        String password = jenkinsHelper.harborPassword;
        String authString = name + ":" + password;
        byte[] authEncBytes = Base64.encodeBase64(authString.getBytes("utf-8"));
        String authStringEnc = new String(authEncBytes);
        Map<String, String> head = new HashMap<>();
        head.put("Authorization", "Basic " + authStringEnc);
        String harborUrl = StringUtils.replace(jenkinsHelper.harborList, "@project", publishList.getName()).
                replace("@env", os);
        String harborlistResult = httptemplate.doGet(harborUrl, null, head);
        log.warn("【编译项目：{},harborUrl:{},调用Harbor获取结果:{}】", publishList.getName(), harborUrl, harborlistResult);
        if (harborlistResult.indexOf("[") > -1 && harborlistResult.indexOf("]") > -1) {
            List<HarBorBo> harBorBos = JSONObject.parseArray(harborlistResult, HarBorBo.class);
            if (harBorBos.size() == 0) {
                throw new ServerException("HR009", "请重新编译版本！");
            }
            List<String> nameList = harBorBos.stream().map(HarBorBo::getName).collect(Collectors.toList());
            List<ListBuild> listBuilds = listBuildMapper.selectByHarborArray(nameList);
            if (listBuilds == null || listBuilds.isEmpty()) {
                throw new ServerException("HR009", "请重新编译版本！");
            }
            serverVo.setListBuilds(listBuilds);
        } else {
            throw new ServerException("HR009", "请重新编译版本！");
        }
        //应用下所有的服务器
        List<Server> serverList = serverMapper.listByTeam(team);
        //获取应用下已经使用过的ip
        List<ListDeployServer> listDeployServerList = listDeployServerMapper.selectBylistid(publishList.getId());

        //实例化
        serverVo.setServerList(serverList);
        serverVo.setBeforeServers(listDeployServerList);
        return serverVo;
    }
}
