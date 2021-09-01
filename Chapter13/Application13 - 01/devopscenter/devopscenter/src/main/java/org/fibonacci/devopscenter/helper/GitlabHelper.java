package org.fibonacci.devopscenter.helper;

import com.alibaba.fastjson.JSONObject;
import org.fibonacci.devopscenter.constants.GitlabConstants;
import org.fibonacci.framework.exceptions.HttpClientException;
import org.fibonacci.framework.exceptions.ServerException;
import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import model.bo.GitLabBackBo;
import model.bo.GitLabProjectsBo;
import org.apache.commons.lang3.StringUtils;
import org.fibonacci.framework.httpclient.HttpClientTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author krame
 * @description： gitlab
 * @date ：Created in 2019-09-17 14:51
 */
@Component
@Slf4j
public class GitlabHelper extends CommonHelper {

    @Autowired
    private HttpClientTemplate httpClientTemplate;

    private GitlabHelper() {

    }

    @Resource
    private GitlabConstants gitlabConstants;


    /**
     * 获取gitlab列表
     */
    public List<GitLabBackBo> listGitlabs(String applicationId, String env) {

        Assert.notNull(applicationId);
        Assert.notNull(env);
        String applicationEvent = gitlabConstants.gitlabCommits.replace("@ids", applicationId).replace("@env", env);
        String result = null;
        try {
            result = httpClientTemplate.doGetClean(applicationEvent,null);
        } catch (HttpClientException e) {
            log.error("获取gitlab列表失败", e);
            if (e.getMessage().indexOf("404 Project Not Found") > -1) {
                throw new ServerException("10003", "调用gitlab获取应用信息错误");
            }else{
                throw new ServerException("10004", "调用gitlab获取应用异常");
            }
        }

        if (StringUtils.isBlank(result)){
            return null;
        }
        return JSONObject.parseArray(result, GitLabBackBo.class);

    }

    /**
     * 获取gitlab的id
     */
    public List<GitLabProjectsBo> getGitlab(String applicationName) {

        Assert.notNull(applicationName);
        String gitlabUrl = gitlabConstants.gitlabProjects.replace("@system", applicationName);
        String result = httpClientTemplate.doGet(gitlabUrl);
        if (StringUtils.isBlank(result))
            return null;
        return JSONObject.parseArray(result, GitLabProjectsBo.class);

    }

}
