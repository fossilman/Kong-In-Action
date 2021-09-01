package org.fibonacci.routeplus.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Builder;
import lombok.Data;
import org.fibonacci.framework.httpclient.HttpClientTemplate;
import org.fibonacci.routeplus.common.bo.TargetBo;
import org.fibonacci.routeplus.common.bo.UpstreamBo;
import org.fibonacci.routeplus.constants.KongConstants;
import org.fibonacci.routeplus.model.bo.KongRouteBo;
import org.fibonacci.routeplus.model.bo.KongServiceBo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
class CreateKongServiceRequest {
    private String applicationName;
    @Builder.Default
    private List<String> gateways = Arrays.asList("pc", "web", "openapi");
    private List<Server> servers;
}

@Data
class Server {
    private String ip;
    private String port;
    private Long vagrancy;
}

@Service
public class KongService {
    @Resource
    protected HttpClientTemplate httpClientTemplate;
    @Resource
    private KongConstants kongConstants;
//    @Value("${kongapi.url}")
    public String kongapiUrl;

    public void createKongService(CreateKongServiceRequest request) {
        String applicationName = request.getApplicationName();

        Map<String,Object> upstreamParam = new HashMap<String,Object>(){{
            put("name", applicationName);
            put("healthchecks", new HashMap(){{
                put("active", new HashMap(){{
                    put("http_path", "/" + applicationName + "/home");
                    put("timeout", 1);
                    put("concurrency", 10);
                    put("healthy", new HashMap(){{
                        put("interval", 3);
                        put("successes", 1);
                    }});
                    put("unhealthy", new HashMap(){{
                        put("interval", 3);
                        put("timeouts", 30);
                    }});
                }});
            }});

        }};
        // 创建 upstream
        String createUpstreamUrl = kongapiUrl + "/upstreams";
        httpClientTemplate.doPost(createUpstreamUrl, upstreamParam);
        // 为upstream 创建 target
        String createTargetUrl = kongapiUrl + "/upstreams/" +applicationName + "/targets";
        for (Server server : request.getServers()) {
            TargetBo targetBo = TargetBo.builder()
                    .upstreamName(applicationName)
                    .target(server.getIp() + ":" + server.getPort())
                    .weight(server.getVagrancy()).build();
            httpClientTemplate.doPost(createTargetUrl, targetBo);
        }

        // 创建 kong service
        KongServiceBo kongServiceBo = KongServiceBo.builder()
                .name(applicationName)
                .host(applicationName)
                .port(8080)
                .build();

        String createKongServiceUrl = kongapiUrl + "/services";
        String respKongServiceInfo = httpClientTemplate.doPost(createKongServiceUrl, kongServiceBo);

        JSONObject kongServiceInfo = JSON.parseObject(respKongServiceInfo);
        String kongServiceId = kongServiceInfo.getString("id");

        // 创建 kong route
        KongRouteBo routeBo = KongRouteBo.builder()
                .service(
                        KongRouteBo.ServiceBean.builder()
                                .id(kongServiceId).build())
                .name(applicationName)
                .headers(
                        KongRouteBo.GatewayHeader.builder()
                                .gateway(
                                        request.getGateways()
                                )
                                .build()
                )
                .paths(new String[]{"/" + applicationName})
                .build();

        String createKongRouteUrl = kongapiUrl + "/routes";
        httpClientTemplate.doPost(createKongRouteUrl, routeBo);
    }

}
