package org.fibonacci.devopscenter.service;

import org.fibonacci.framework.httpclient.HttpClientTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class BuildService {
    @Resource
    private HttpClientTemplate httpClientTemplate;
    @Value("${jenkins.url}")
    private String jenkinsUrl;
    //初始化请求头鉴权
    private static final Map<String, String> httpHeaders=new HashMap(){{
        put("Authorization", "Basic " + Base64.getUrlEncoder().encodeToString(("jenkins:11e39e188034713db9f00f2b44c59d65af").getBytes()));
    }};
    public void build(String gitlabVersion, String applicationName, String env) {
        StringBuilder param = new StringBuilder();
        param.append("TAG").append("=").append(gitlabVersion)
                .append("&NAME").append("=").append(applicationName)
                .append("&ENV").append("=").append(env);
        httpClientTemplate.doPost(jenkinsUrl+"/job/"+applicationName+"/buildWithParameters?" + param,null, httpHeaders);
    }
}
