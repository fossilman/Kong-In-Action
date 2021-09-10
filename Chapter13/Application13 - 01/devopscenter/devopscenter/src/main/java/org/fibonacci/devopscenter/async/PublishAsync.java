package org.fibonacci.devopscenter.async;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import model.bo.PublishBo;
import model.bo.ServerBo;
import model.bo.ShellBo;
import org.fibonacci.framework.exceptions.ServerException;
import org.fibonacci.framework.global.AppInfo;
import org.fibonacci.framework.httpclient.HttpClientTemplate;
import org.fibonacci.routeplus.common.bo.RoutePlusBo;
import org.fibonacci.routeplus.rpc.feign.RouteFeignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author krame
 * @description： Kong异步处理
 * @date ：Created in 2019-08-14 11:37
 */
@Slf4j
@Component
public class PublishAsync {

    @Resource
    private RouteFeignClient routeFeignClient;

    @Resource
    private HttpClientTemplate httpClientTemplate;

    @Resource
    private AppInfo appInfo;

    @Value("${harbor.address}")
    private String harborAddress;

    /**
     * 异步处理kong逻辑
     *
     * @param
     */
    @Async("taskExecutor")
    public void executeKong(PublishBo rublishBo) {

        if (rublishBo.getServerList() != null) {

            List<RoutePlusBo.ServerBo> serverList = new ArrayList<>();
            RoutePlusBo.ServerBo serverBo = null;
            for (ServerBo sBo : rublishBo.getServerList()) {
                serverBo = new RoutePlusBo.ServerBo();
                serverBo.setIp(sBo.getIp());
                serverBo.setPort(sBo.getPort());
                serverBo.setVagrancy(sBo.getVagrancy());
                serverList.add(serverBo);
            }

            //实例化
            RoutePlusBo routePlusBo = new RoutePlusBo();
            routePlusBo.setGatewayType(rublishBo.getGatewayType());
            routePlusBo.setName(rublishBo.getName());
            routePlusBo.setPublishType(rublishBo.getPublishType());
            routePlusBo.setServerList(serverList);
            routeFeignClient.deployKong(routePlusBo);
        }
    }

    /**
     * 异步处理kong逻辑
     *
     * @param
     */
    @Async("taskExecutor")
    public void removeKong(String name) {
        RoutePlusBo build = RoutePlusBo.builder().name(name).build();
        routeFeignClient.removeKong(build);
    }

    /**
     * 异步处理kong逻辑
     *
     * @param
     */
    @Async("taskExecutor")
    public void updateRoute(String name,String routeType) {
        routeFeignClient.updateRoute(name, routeType);
    }

    @Async("taskExecutor")
    public void pullImages(String url, String[] params,String name,String remark){
        try {
            String pullUrl = "http://" +url + ":4789/images/create?fromImage=" + params[0] + "&tag=" + params[1] + "&fromSrc=http://" + harborAddress;
            //拉取镜像
            httpClientTemplate.doPost(pullUrl, (Object) null);
            String verifyPullUrl ="http://"+ url + ":4789/images/json";
            String res = httpClientTemplate.doGet(verifyPullUrl);
            //验证是否拉取成功
            while (!res.contains(params[1])) {
                res = httpClientTemplate.doGet(verifyPullUrl);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String os = appInfo.getEnv();
            //创建容器
            String createUrl ="http://"+ url +  ":4789/containers/create?name=" + name;
            String creatParams = "{" +
                    "\"Hostname\":\"" + name + "_" + os + "\"," +
                    "\"User\": \"root\"," +
                    "\"Env\": [" +
                    "\"SERVER_HOST=" + url + "\"," +
                    "\"SERVER_ENV=" + os + "\"," +
                    "\"SERVER_PORT=" + "8080" + "\"" +
                    "]," +
                    "\"Volumes\": {" +
                    "\"/data/logs\": {}" +
                    "}," +
                    "\"ExposedPorts\": {" +
                    " \"8080/tcp\": {}" +
                    " }," +
                    "\"WorkingDir\": \"/data\"," +
                    "\"Image\": \"" + remark + "\"," +
                    "\"HostConfig\": {" +
                    "\"Binds\": [" +
                    "\"/data/logs/:/data/logs/\"," +
                    "\"/etc/localtime:/etc/localtime:ro\"" +
                    "]," +
                    "Privileged:true,"+
                    "\"NetworkMode\": \"host\"," +
                    "\"RestartPolicy\": {" +
                    "\"Name\": \"on-failure\"," +
                    "\"MaximumRetryCount\": 5" +
                    "}" +
                    "}" +
                    "}";
            JSONObject jsonObject = JSON.parseObject(creatParams);
            res = httpClientTemplate.doPost(createUrl, jsonObject);
            JSONObject createResJson = JSON.parseObject(res);
            String id = createResJson.get("Id").toString();
            //重启容器
            String restartUrl ="http://"+ url + ":4789/containers/" + id + "/restart";
            httpClientTemplate.doPost(restartUrl,(Object)null);
        }
        catch (Exception e){
            throw new ServerException("10099", "调用publish:shell发布镜像失败，返回msg："+e.getMessage());
        }
    }

    @Async("taskExecutor")
    @Deprecated
    public Future<ShellBo> verifyDeploy(String url, String name){
        int i=0;
        ShellBo shellBo = new ShellBo();
        shellBo.setIp(url);
        shellBo.setResult("SUCCESS");
        String verifyUrl="http://"+url+":8080/"+name+"/actuator/health";
        String res="";
        try{
            log.info("发送check请求"+verifyUrl);
            res=httpClientTemplate.doGet(verifyUrl);
            log.info("请求结果："+res);
        }
        catch(Exception e){

        }
        if(res.contains("UP")) {
            shellBo.setResult("SUCCESS");
        } else {
            shellBo.setResult("FAIL");
        }
//        while(!res.contains("UP")&&i<6){
//            try{
//                Thread.sleep(20000);
//            }
//            catch (Exception e){
//
//            }
//            try{
//                log.info("发送check请求1"+verifyUrl);
//                res=httpClientTemplate.doGet(verifyUrl);
//                log.info("请求结果1："+res);
//            }
//            catch (Exception e){
//            }
//
//            shellBo.setResult("ING");
//            i++;
//        }
//        if(i>=6){
//            shellBo.setResult("FAIL");
//        }
        return new AsyncResult<>(shellBo);
    }

    public ShellBo syncVerifyDeploy(String url, String name){
        int i=0;
        ShellBo shellBo = new ShellBo();
        shellBo.setIp(url);
        shellBo.setResult("SUCCESS");
        String verifyUrl="http://"+url+":8080/"+name+"/actuator/health";
        String res="";
        try{
            log.info("发送check请求"+verifyUrl);
            res=httpClientTemplate.doGet(verifyUrl);
            log.info("请求结果："+res);
        }
        catch(Exception e){

        }
        if(res.contains("UP")) {
            shellBo.setResult("SUCCESS");
        } else {
            shellBo.setResult("FAIL");
        }
        return shellBo;
    }


}
