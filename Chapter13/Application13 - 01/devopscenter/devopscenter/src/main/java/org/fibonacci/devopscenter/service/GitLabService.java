package org.fibonacci.devopscenter.service;

import org.fibonacci.devopscenter.constants.BaseConstants;
import org.fibonacci.devopscenter.helper.GitlabHelper;
import org.fibonacci.framework.exceptions.ServerException;
import org.fibonacci.framework.global.AppInfo;
import lombok.extern.slf4j.Slf4j;
import model.GitLabVo;
import model.bo.GitLabBackBo;
import model.bo.GitLabProjectsBo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
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
public class GitLabService {

    @Resource
    private GitlabHelper gitlabHelper;

    @Resource
    private AppInfo appInfo;

    /**
     * 应用id
     *
     * @param applicationId
     * @return
     */
    public GitLabVo projectsInfo(Integer applicationId) {


        String env = BaseConstants.GitEnv.throughtNameGetValue(appInfo.getEnv());
        log.info("获取环境名称：" + env);

        List<GitLabVo.GitlabInfo> gitlabInfoLists = new ArrayList<>();
        List<GitLabBackBo> gitLabBackBoList = gitlabHelper.listGitlabs(applicationId + "", env);

        if (gitLabBackBoList != null) {
            gitLabBackBoList.stream().filter(gitLabBacks -> gitLabBacks != null).forEach(gitLabBack -> {
                GitLabVo.GitlabInfo gitlabInfo = new GitLabVo.GitlabInfo();
                String commitTitle = gitLabBack.getTitle();
                gitlabInfo.setVersion(gitLabBack.getId());
                gitlabInfo.setAuthor(gitLabBack.getCommitter_name());
                if (!StringUtils.isBlank(commitTitle)) {
                    gitlabInfo.setHeadDesc(commitTitle.indexOf("<body>") == -1 ? commitTitle : commitTitle.substring(0, commitTitle.indexOf("<body>")));
                }
                gitlabInfo.setBodyDesc(commitTitle);
                gitlabInfoLists.add(gitlabInfo);
            });
        }

        //构建返回对象
        GitLabVo gitLabVo = new GitLabVo();
        gitLabVo.setApplicationId(applicationId);
        gitLabVo.setGitlabInfo(gitlabInfoLists);
        return gitLabVo;
    }


    /**
     * 获取gitlab的应用id
     *
     * @param
     * @return
     */
    public Integer getGitLabInfo(String applicationName) {

        List<GitLabProjectsBo> gitLabBackBoList = gitlabHelper.getGitlab(applicationName);
        if (gitLabBackBoList == null) {
            throw new ServerException("10003", "通过应用名称：" + applicationName + ",获取应用id不存在！");
        }
        Map<String, List<GitLabProjectsBo>> gitLabBackBoMap = gitLabBackBoList.stream().collect(Collectors.groupingBy(GitLabProjectsBo::getName));
        if (gitLabBackBoMap == null || gitLabBackBoMap.get(applicationName) == null) {
            throw new ServerException("10003", "通过应用名称：" + applicationName + ",获取应用id不存在！");
        }
        return gitLabBackBoMap.get(applicationName).get(0).getId();

    }

}
