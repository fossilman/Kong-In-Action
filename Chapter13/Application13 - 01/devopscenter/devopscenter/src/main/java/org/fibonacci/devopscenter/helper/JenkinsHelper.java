package org.fibonacci.devopscenter.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.offbytwo.jenkins.JenkinsServer;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.fibonacci.devopscenter.constants.PublishConstants;
import org.fibonacci.framework.exceptions.ServerException;
import org.fibonacci.framework.httpclient.HttpClientTemplate;
import org.fibonacci.framework.util.ResultCode;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author krame
 * @description：
 * @date ：Created in 2019-09-17 14:51
 */
@Component
@Slf4j
public class JenkinsHelper implements InitializingBean {

    private JenkinsHelper() {
    }

    // 连接 Jenkins 需要设置的信息
    @Value("${jenkins.url}")
    private String jenkinsUrl;

    @Value("${jenkins.username}")
    private static String jenkinsUsername="jenkins";

    @Value("${jenkins.password}")
    private static String jenkinsPassword="H10HTGIYQhNM";

    @Value("${harbor.url}")
    public String harborUrl;

    @Value("${harbor.user}")
    public String harborUser;


    @Value("${harbor.password}")
    public String harborPassword;


    @Value("${harbor.list}")
    public String harborList;

    @Value("${harbor.shell.url}")
    public String harborShellUrl;


    private JenkinsServer jenkinsServer = null;

    @Resource
    private HttpClientTemplate httpClientTemplate;
    //初始化请求头鉴权
    private static final Map<String, String> httpHeaders=new HashMap(){{
        put("Authorization", "Basic " + Base64.getUrlEncoder().encodeToString(("jenkins:11e39e188034713db9f00f2b44c59d65af").getBytes()));
    }};
    /**
     * 构建jenkins
     *
     * @return
     */

    public Integer build(String applicationName, Map<String, Object> params) throws IOException, URISyntaxException {

        int nexjobId = 0;
        String res="";
        try{
            res=httpClientTemplate.doPost(jenkinsUrl+"/job/"+applicationName+"/api/json?pretty=true",params,httpHeaders);
            JSONObject jsonObject= JSON.parseObject(res);
            JSONObject nextId= (JSONObject) JSON.toJSON(jsonObject.get("lastBuild"));
            nexjobId=1;
            if(nextId!=null){
                nexjobId=Integer.parseInt(nextId.get("number").toString())+1;
            }
        }
        catch (Exception e) {
            throw new ServerException("10003", "获取jenkins镜像失败，请填写正确应用名称！");
        }
        String param=paramsChange(params);

        try{
            res=httpClientTemplate.doPost(jenkinsUrl+"/job/"+applicationName+"/buildWithParameters?"+param,(Object)null,httpHeaders);
        }
        catch (Exception e) {
            //e.printStackTrace();
            log.error("调用jekins编译失败", e);
            throw new ServerException("10003", "调用jekins编译失败，msg：=" + e.getMessage());
        }

        //int nexjobId = 0;
        //String res="";
        //try{
        //    String param=paramsChange(params);
        //    param=param.concat("&Jenkins-Crumb=1fea754f956f712da94dbab9291e56ab5475133764e0711f1dfecbc5fb256737");
        //    res=httpClientTemplate.doPost(jenkinsUrl+"/job/"+applicationName+"/buildWithParameters?"+param,(Object)null,httpHeaders);;
        //
        //    JSONObject jsonObject= JSON.parseObject(res);
        //    JSONObject nextId= (JSONObject) JSON.toJSON(jsonObject.get("lastBuild"));
        //    nexjobId=1;
        //    if(nextId!=null){
        //        nexjobId=Integer.parseInt(nextId.get("number").toString())+1;
        //    }
        //}
        //catch (Exception e) {
        //    //throw new ServerException("10003", "获取jenkins镜像失败，请填写正确应用名称！");
        //    log.error("调用jekins编译失败", e);
        //    throw new ServerException("10003", "调用jekins编译失败，msg：=" + e.getMessage());
        //}

        return nexjobId;
    }


    /**
     * 检查构建jenkins
     *
     * @return
     */
    public ResultCode<String> checkBuild(String applicationName, Integer jobId) {
        String res="";
        int nexjobId=0;
        try{
            res=httpClientTemplate.doPost(jenkinsUrl+"/job/"+applicationName+"/api/json?pretty=true",(Object) null,httpHeaders);
            JSONObject jsonObject= JSONObject.parseObject(res);
            JSONObject nextId= (JSONObject) JSON.toJSON(jsonObject.get("lastBuild"));
            if(nextId!=null){
                nexjobId=Integer.parseInt(nextId.get("number").toString());
            }
        }
        catch (Exception e) {
            throw new ServerException("10003", "获取jenkins镜像失败，请填写正确应用名称！");
        }
        try{
            res=httpClientTemplate.doPost(jenkinsUrl+"/job/"+applicationName+"/"+nexjobId+"/api/json",(Object) null,httpHeaders);
            JSONObject jsonObject=JSONObject.parseObject(res);
            String building=jsonObject.getString("building");
            String status=jsonObject.getString("result");
            if(building.equals("false")&&status.equals("SUCCESS")){
                return ResultCode.getSuccessReturn(null, null, PublishConstants.BUILD_STATUS.SUCCESS.name());
            }
            //当前日志
            res=httpClientTemplate.doPost(jenkinsUrl+"/job/"+applicationName+"/"+nexjobId+"/logText/progressiveText",(Object) null,httpHeaders);
            String[] resList=res.split("\r\n");
            String lastLine=resList[resList.length-1];
            if(lastLine.length()!=17){
                return ResultCode.getFailureReturn(null, "ING",
                        PublishConstants.BUILD_STATUS.ING.name());
            }
            String lastTag=lastLine.substring(0,8);
            String lastResult=lastLine.substring(10,17);
            //jenkins运行中没结束

            if (!lastTag.equals("Finished") || StringUtils.isBlank(status)) {
                return ResultCode.getFailureReturn(null, "ING",
                        PublishConstants.BUILD_STATUS.ING.name());
            }
            //jenkins编译失败
            if(!lastResult.equals("SUCCESS")||!status.equals("SUCCESS")){
                res="";
                for(int i=0;i<resList.length;i++){
                    if(resList[i].contains("failure")||resList[i].contains("ERR")||resList[i].contains("ERROR")){
                        res+=resList[i];
                    }
                }
                return ResultCode.getFailureReturn(null,res,
                        PublishConstants.BUILD_STATUS.FAIL.name());
            }
            return ResultCode.getSuccessReturn(null, null, PublishConstants.BUILD_STATUS.SUCCESS.name());
        }
        catch(Exception e){
            return ResultCode.getFailureReturn(null, "通过jenkins-jobid：" + jobId + "，获取不到Job实例",
                    PublishConstants.BUILD_STATUS.ING.name());
        }

/*        try {
            JenkinsServer jenkinsServer = connection();
            JobWithDetails job = jenkinsServer.getJob(applicationName);
            if (job == null) {
                throw new ServerException("10003", "获取jenkins镜像失败，请填写正确应用名称！");
            }
            Build build = job.getBuildByNumber(16);
            if (build == null) {
                return ResultCode.getFailureReturn(null, "通过jenkins-jobid：" + jobId + "，获取不到Job实例",
                        PublishConstants.BUILD_STATUS.ING.name());
            } else {
                BuildWithDetails buildWithDetails = build.details();
                // 当前日志
                ConsoleLog currentLog = buildWithDetails.getConsoleOutputText(0);
                String consoleLog = currentLog.getConsoleLog();
                if (consoleLog.length() > 9216) {
                    int start = consoleLog.length() - 9216;
                    consoleLog = consoleLog.substring(start, consoleLog.length());
                }

                //jenkins运行中没结束
                if (buildWithDetails.getResult() == null || currentLog.getHasMoreData()) {
                    return ResultCode.getFailureReturn(null, consoleLog,
                            PublishConstants.BUILD_STATUS.ING.name());
                }

                //jenkins编译失败
                String status = buildWithDetails.getResult().name();
                if (!StringUtils.equals(status, PublishConstants.BUILD_STATUS.SUCCESS.name())) {
                    if (!StringUtils.equals(status, PublishConstants.BUILD_STATUS.ING.name())) {
                        return ResultCode.getFailureReturn(null, consoleLog,
                                PublishConstants.BUILD_STATUS.FAIL.name());
                    } else {
                        return ResultCode.getFailureReturn(null, consoleLog,
                                PublishConstants.BUILD_STATUS.ING.name());
                    }
                }
                return ResultCode.getSuccessReturn(null, null, status);
            }
        } catch (Exception e) {
            log.error("调用jenkins-check失败", e);
            return ResultCode.getFailureReturn(null, e.getMessage(),
                    PublishConstants.BUILD_STATUS.FAIL.name());
        }*/
    }

    /**
     * 获取正在执行构建任务的日志信息
     */
    public void getBuildActiveLog(String name) {
        String res="";
        int nexjobId=0;
        try {
            //获取最后一次编译id
            res=httpClientTemplate.doPost(jenkinsUrl+"/job/"+name+"/api/json?pretty=true",(Object) null,httpHeaders);
            JSONObject jsonObject= JSONObject.parseObject(res);
            JSONObject nextId= (JSONObject) JSON.toJSON(jsonObject.get("lastBuild"));
            nexjobId=Integer.parseInt(nextId.get("number").toString());

            //获取当前日志
            res=httpClientTemplate.doPost(jenkinsUrl+"/job/"+name+"/"+nexjobId+"/logText/progressiveText",(Object) null,httpHeaders);
            String[] resList=res.split("\r\n");
            String lastLine=resList[resList.length-1];
            while(lastLine.length()!=17||!lastLine.substring(0,8).equals("Finished")||!lastLine.substring(10,17).equals("SUCCESS")){
                //获取最新的日志
                res=httpClientTemplate.doPost(jenkinsUrl+"/job/"+name+"/"+nexjobId+"/logText/progressiveText",(Object) null,httpHeaders);
                resList=res.split("\r\n");
                lastLine=resList[resList.length-1];
                // 睡眠1s
                Thread.sleep(1000);
            }


            /*// 这里用最后一次编译来示例
            BuildWithDetails build = jenkinsServer.getJob(name).getLastBuild().details();
            // 当前日志
            ConsoleLog currentLog = build.getConsoleOutputText(0);
            // 输出当前获取日志信息
            System.out.println(currentLog.getConsoleLog());
            // 检测是否还有更多日志,如果是则继续循环获取
            while (currentLog.getHasMoreData()) {
                // 获取最新日志信息
                ConsoleLog newLog = build.getConsoleOutputText(currentLog.getCurrentBufferSize());
                // 输出最新日志
                System.out.println(newLog.getConsoleLog());
                currentLog = newLog;
                // 睡眠1s
                Thread.sleep(1000);
            }*/
        } catch ( InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Http 客户端工具
     * <p>
     * 如果有些 API 该Jar工具包未提供，可以用此Http客户端操作远程接口，执行命令
     *
     * @return
     */
    /*public JenkinsHttpClient getClient() {

        try {
            if (jenkinsHttpClient == null) {
                jenkinsHttpClient = new JenkinsHttpClient(new URI(jenkinsUrl), jenkinsUsername, jenkinsPassword);
            }
        } catch (URISyntaxException e) {
            log.error("连接jenkins异常", e);
        }
        return jenkinsHttpClient;
    }*/

    /**
     * 连接 Jenkins
     */
    public JenkinsServer connection() {
        try {
            if (jenkinsServer == null) {
                jenkinsServer = new JenkinsServer(new URI(jenkinsUrl), jenkinsUsername, jenkinsPassword);
            }
        } catch (URISyntaxException e) {
            log.error("连接jenkins异常", e);
        }
        return jenkinsServer;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        //init
        jenkinsServer = new JenkinsServer(new URI(jenkinsUrl), jenkinsUsername, jenkinsPassword);
        //log.warn("初始化Jenkins......");
        /*if (jenkinsServer == null) {
            log.error("初始化jenkins失败...");
        }*/


    }

    private static String paramsChange(Map<String,Object> params){
        String param="";
        for(Map.Entry<String,Object> entry:params.entrySet()){
            param=param+"&"+entry.getKey()+"="+entry.getValue();
        }
        String[] par=param.split("&");
        return  par[1]+"&"+par[2]+"&"+par[3];
    }

}
