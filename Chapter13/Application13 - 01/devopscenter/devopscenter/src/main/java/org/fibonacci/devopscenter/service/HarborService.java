package org.fibonacci.devopscenter.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.fibonacci.framework.global.AppInfo;
import org.fibonacci.framework.httpclient.HttpClientTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class HarborService {

    @Resource
    private HttpClientTemplate httpClientTemplate;
    @Resource
    private AppInfo appInfo;
    @Value("${harbor.address}")
    private String harborAddress;
    @Value("${container.port:4789}")
    private String containerPort;

    public void pullImages(String containerIp, String applicationName, String gitVersion) {
        String imageName = harborAddress + "/" + applicationName + "/" + applicationName + appInfo.getEnv();
        String imageTag = gitVersion;
        StringBuilder pullRequest = new StringBuilder();
        pullRequest.append("http://").append(containerIp).append(":").append(containerPort)
                .append("/images/create?fromImage=").append(imageName)
                .append("&tage=").append(imageTag)
                .append("&fromSrc=").append(harborAddress);
        //拉取镜像
        httpClientTemplate.doPost(pullRequest.toString(), null);
    }

    public void runContainer(String containerIp, String applicationName, String gitVersion) {























































































































































        String imageName = harborAddress + "/" + applicationName + "/" + applicationName + appInfo.getEnv();
        String imageTag = gitVersion;

        // 创建容器
        String createContainerUrl = "http://" + containerIp + ":" + containerPort + "/containers/create?name=" + applicationName;

        Map<String,Object> createParams = new HashMap() {{
            put("WorkingDir", "/data");
            put("User", "root");
            put("Volumes", new HashMap(){{
                put("/data/logs", new HashMap<>());
            }});
            put("Hostname", applicationName + "_" + appInfo.getEnv());
            put("HostConfig", new HashMap(){{
                put("Binds", new ArrayList<String>(){{
                    add("/data/logs/:/data/logs/");
                    add("/etc/localtime:/etc/localtime:ro");}});
                put("Privileged", true);
                put("RestartPolicy", new HashMap(){{
                    put("MaximumRetryCount", 5);
                    put("Name", "on-failure");
                }});
                put("NetworkMode", "host");
            }});
            put("Env", new ArrayList(){{
                add("SERVER_HOST=" + containerIp);
                add("SERVER_ENV=" + appInfo.getEnv());
                add("SERVER_PORT=8080");
            }});
            put("Image", imageName + ":" + imageTag);
            put("ExposedPorts", new HashMap(){{
                put("8080/tcp", new HashMap<>());
            }});
        }};

        String resp = httpClientTemplate.doPost(createContainerUrl, createParams);
        String containerId = JSON.parseObject(resp).getString("Id");

        // 启动容器
        String restartContainerUrl = "http://" + containerIp + ":" + containerPort + "/containers/" + containerId + "/restart";
        httpClientTemplate.doPost(restartContainerUrl, null);




    }

    public static void main(String[] args) {
                Map<String,Object> creatParams = ImmutableMap.<String,Object>builder()
                .put("WorkingDir", "/data")
                .put("User", "root")
                .put("Volumes", ImmutableMap.builder().put("/data/logs", new HashMap<>()).build())
                .put("Hostname", "$applicationName" + "_" + "$appInfo.getEnv()")
                .put("HostConfig", ImmutableMap.builder()
                        .put("Binds", Lists.newArrayList("/data/logs/:/data/logs/", "/etc/localtime:/etc/localtime:ro"))
                        .put("Privileged", true)
                        .put("RestartPolicy", ImmutableMap.builder().put("MaximumRetryCount", 5).put("Name", "on-failure").build())
                        .put("NetworkMode", "host").build())
                .put("Env", Lists.newArrayList("SERVER_HOST=" + "$containerIp", "SERVER_ENV=" + "$appInfo.getEnv()", "SERVER_PORT=8080"))
                .put("Image", "$imageName:$imageTag")
                .put("ExposedPorts", ImmutableMap.builder().put("8080/tcp", new HashMap<>()).build())
                .build();
        System.out.println(JSON.toJSONString(creatParams));
        Map<String,Object> creatParams_ = new HashMap() {{
            put("WorkingDir", "/data");
            put("User", "root");
            put("Volumes", new HashMap(){{
                put("/data/logs", new HashMap<>());
            }});
            put("Hostname", "$applicationName" + "_" + "$appInfo.getEnv()");
            put("HostConfig", new HashMap(){{
                put("Binds", new ArrayList<String>(){{
                    add("/data/logs/:/data/logs/");
                    add("/etc/localtime:/etc/localtime:ro");}});
                put("Privileged", true);
                put("RestartPolicy", new HashMap(){{
                    put("MaximumRetryCount", 5);
                    put("Name", "on-failure");
                }});
                put("NetworkMode", "host");
            }});
            put("Env", new ArrayList(){{
                add("SERVER_HOST=" + "$containerIp");
                add("SERVER_ENV=" + "$appInfo.getEnv()");
                add("SERVER_PORT=8080");
            }});
            put("Image", "$imageName" + ":" + "$imageTag");
            put("ExposedPorts", new HashMap(){{
                put("8080/tcp", new HashMap<>());
            }});
        }};
        System.out.println(JSON.toJSONString(creatParams_));


    }
}
