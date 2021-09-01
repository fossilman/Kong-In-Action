package org.fibonacci.devopscenter.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import model.bo.PublishBo;
import model.bo.ServerBo;
import model.bo.ShellBo;
import model.bo.VersionBo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.fibonacci.devopscenter.async.PublishAsync;
import org.fibonacci.devopscenter.configuration.Configs;
import org.fibonacci.devopscenter.constants.PublishConstants;
import org.fibonacci.devopscenter.constants.ShellConstants;
import org.fibonacci.devopscenter.domain.ListBuild;
import org.fibonacci.devopscenter.domain.ListDeploy;
import org.fibonacci.devopscenter.domain.ListDeployServer;
import org.fibonacci.devopscenter.domain.PublishList;
import org.fibonacci.devopscenter.mapper.ListBuildMapper;
import org.fibonacci.devopscenter.mapper.ListDeployMapper;
import org.fibonacci.devopscenter.mapper.ListDeployServerMapper;
import org.fibonacci.devopscenter.mapper.PublishListMapper;
import org.fibonacci.devopscenter.utils.BeanUtils;
import org.fibonacci.framework.exceptions.ServerException;
import org.fibonacci.framework.global.AppInfo;
import org.fibonacci.framework.httpclient.HttpClientTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

//import edu.jiahui.inneruser.response.LoginRsp;

/**
 * @author krame
 * @description： 发布帮助类
 * @date ：Created in 2019-09-17 14:51
 */
@Component
@Slf4j
public class PublishHelper extends DeployAbstract {


    @Resource
    private ShellConstants shellConstants;


    @Resource
    private ShellHelper shellHelper;


    @Resource
    private PublishListMapper publishListMapper;


    @Resource
    private ListBuildMapper listBuildMapper;

    @Resource
    private ListDeployMapper listDeployMapper;

    @Resource
    private ListDeployServerMapper listDeployServerMapper;

    @Resource
    private LoginHelper loginHelper;

    @Resource
    private PublishAsync publishAsync;

    @Resource
    private AppInfo appInfo;

    @Resource
    private HttpClientTemplate httpClientTemplate;

    @Value("${harbor.address}")
    private String harborIp;

    @Autowired
    private Configs configs;
    /**
     * 处理发布逻辑
     *
     * @param publishBo
     */
    @Override
    public String executeDeployLog(PublishBo publishBo) {

        ////获取上次的deploy记录
        //LoginRsp login = loginHelper.parseJwtTokenLogin();

        StringBuffer stringBuffer = new StringBuffer("预发布日志执行顺序详情输出：");
        stringBuffer.append("<br/>");

        ListDeploy listDeploy = null;
        List<ListDeploy> listDeploys = listDeployMapper.selectBylistid(publishBo.getId(), null);
        if (listDeploys.size() > 0) {
            listDeploy = listDeploys.get(0);

            List<ListDeployServer> listDeployServers = listDeployServerMapper.selectByDeployid(listDeploy.getId());
            List<String> beforeIpList = listDeployServers.stream().map(ListDeployServer::getServerIp).distinct().collect(Collectors.toList());
            if (StringUtils.equals(publishBo.getPublishType(), PublishConstants.DEPLOY_STATUS.SIMPLE.name())) {
                log.info("采用100%发布模式，停掉上次发布版本所有机器ip:{} 应用，释放资源", JSON.toJSONString(beforeIpList));
                stringBuffer.append("<p>*.采用100%发布模式，停掉上次发布版本所有机器ip:[" + JSON.toJSONString(beforeIpList) + "],释放资源");
            } else {
                //List<String> ipList = publishBo.getServerList().stream().map(ServerBo::getIp).distinct().collect(Collectors.toList());
                //beforeIpList.removeAll(ipList);
                log.info("采用新老版本兼容模式，停掉上次版本所有机器ip:{} 应用，释放资源", JSON.toJSONString(beforeIpList));
                stringBuffer.append("<p>*.采用新老版本兼容模式，停掉上次版本所有机器ip:[" + JSON.toJSONString(beforeIpList) + "],释放资源");
            }

            if (beforeIpList != null) {

                beforeIpList.forEach(beforeIp -> {
                    String res = httpClientTemplate.doGet("http://" + beforeIp + ":4789/containers/json?all=true");
                    JSONArray jsonArray = JSON.parseArray(res);
                    JSONObject containersInfo = null;
                    for (Object arry : jsonArray) {
                        if (arry.toString().contains(publishBo.getName())) {
                            containersInfo = (JSONObject) arry;
                        }
                    }
                    if (containersInfo != null && !StringUtils.isBlank(containersInfo.toString())) {
                        String id = containersInfo.get("Id").toString();
                        if (containersInfo.get("State").toString().equals("running")) {
                            httpClientTemplate.doPost("http://" + beforeIp + ":4789/containers/" + id + "/kill", (Object) null);
                        }
                        httpClientTemplate.doDelete("http://" + beforeIp + ":4789/containers/" + id);
                    }


                });
            }

        }

        //发布新老版本兼容模式
        VersionBo versionBo = null;
        if (StringUtils.equals(publishBo.getPublishType(), PublishConstants.DEPLOY_STATUS.COMPATIBILITY.name())) {
            if (listDeploy != null) {

                //算法计算，发布的版本类型
                versionBo = this.calculatedReboot(publishBo);

                stringBuffer.append("<p>*.通过参数分析：[发布机器数量：" + publishBo.getServerList().size() + ",占比：" + publishBo.getVagrancy() + ",Tomcat默认线程数:" + 200 + "]。");
                stringBuffer.append("算法匹配出，发布New新版本机器数量:" + versionBo.getNewVersionRebootNum() + ",版本号:" + publishBo.getGitlabVersion() + ",");
                stringBuffer.append("发布Old老版本机器数量:" + versionBo.getOldVersionRebootNum() + ",版本号:" + publishBo.getBeforeGitlabVersion() + ",");
                stringBuffer.append("New机器Kong详情：【" + JSON.toJSONString(versionBo.getNewRebootLists()) + "】,");
                stringBuffer.append("Old机器Kong详情：【" + JSON.toJSONString(versionBo.getOldRebootLists()) + "】");
                stringBuffer.append("</p>");
                /**
                 * 发布版本的场景罗列
                 * 1.如果待发布的版本是上次发布的版本，如上次发布版本A-新版本,B-老版本,本次发布是C-新版本,B-老版本
                 * 2.如果待发布的版本不是是上次发布的版本，如上次发布版本A-新版本,B-老版本,本次发布是C-新版本,D-老版本
                 */
                //新的版本发布
//                String shellUrl = "ssh -p 10022 root@" + shellConstants.shellFileIp + " sh " + shellConstants.publishUrl + " " + publishBo.getRemark() + " " + versionBo.getNewReboots();
//                stringBuffer.append("<p>*.调用dev_publish:shell脚本命令发布新版本:" + shellUrl + "</p>");
//                //老的版本发布
//                String oldshellUrl = "ssh -p 10022 root@" + shellConstants.shellFileIp + " sh " + shellConstants.publishUrl + " " + publishBo.getBeforeRemark() + " " + versionBo.getOldReboots();
//                stringBuffer.append("<p>*.调用dev_publish:shell脚本命令发布老版本:" + oldshellUrl + "</p>");
                String remark=harborIp+"/"+publishBo.getName()+"/"+publishBo.getName()+appInfo.getEnv()+":"+publishBo.getGitlabVersion();
                String beforeRemark=publishBo.getBeforeRemark();
                String[] oldRebootList=versionBo.getOldReboots().split(" ");
                String[] newRebootList=versionBo.getNewReboots().split(" ");
                for(int i=0;i<oldRebootList.length;i++){
                    String[] params=beforeRemark.split(":");
                    String url=oldRebootList[i];
                    //异步拉取镜像 创建
                    publishAsync.pullImages(url,params,publishBo.getName(),beforeRemark);
                }
                //新
                for(int i=0;i<newRebootList.length;i++){
                    String[] params=remark.split(":");
                    String url=newRebootList[i];
                    //异步拉取镜像 创建
                    publishAsync.pullImages(url,params,publishBo.getName(),remark);
                }


            }
        } else {
            //1.调用shell脚本发布镜像
            String shellParam = publishBo.getServerList().stream().map(ServerBo::getIp).distinct().collect(Collectors.joining(" "));
            String[] rebootList=shellParam.split(" ");
            String remark=harborIp+"/"+publishBo.getName()+"/"+publishBo.getName()+appInfo.getEnv()+":"+publishBo.getGitlabVersion();
            for(int i=0;i<rebootList.length;i++){
                publishAsync.pullImages(rebootList[i], remark.split(":"), publishBo.getName(), remark);

            }
            /*//1.调用shell脚本发布镜像
            String shellParam = publishBo.getServerList().stream().map(ServerBo::getIp).distinct().collect(Collectors.joining(" "));
            //String shellUrl = "ssh root@" + shellConstants.shellFileIp + " sh " + shellConstants.publishUrl + "dev_" + publishBo.getName() + ".sh" + " " + publishBo.getName() + ":" + publishBo.getGitlabVersion() + " " + shellParam;
            String shellUrl = "ssh -p 10022 root@" + shellConstants.shellFileIp + " sh " + shellConstants.publishUrl + " " + publishBo.getBeforeRemark() + " " + shellParam;
            stringBuffer.append("<p>*.调用dev_publish:shell脚本命令:" + shellUrl + "</p>");
            stringBuffer.append("<p>Kong的流量占比各100</p>");*/
        }
        //stringBuffer.append("<p>*.发布人：" + login.getUsername() + "</p>");
        return stringBuffer.toString();
    }

    /**
     * 处理发布逻辑
     * 1.kill容器
     * 2.拉取镜像
     * 3.新建容器
     * 4.启动
     *
     * @param publishBo
     */
    @Override
    public Long executeDeploy(PublishBo publishBo) {

        //获取上次的deploy记录
        //LoginRsp login = loginHelper.parseJwtTokenLogin();


        //1.停掉上次老的应用
        ListDeploy listDeploy = null;
        if (!appInfo.getAppName().equals(publishBo.getName())) {//规避发布自己应用
            List<ListDeploy> listDeploys = listDeployMapper.selectBylistid(publishBo.getId(), null);
            if (listDeploys.size() > 0) {
                listDeploy = listDeploys.get(0);
                //查询上次发布的机器列表和这次发布机器列表比较a,b a
                /**
                 * 涉及场景：
                 * 1.上次发布A版本对应的机器 A\B,本次发布的A版本对应机器 A\B\C,扩充一台机器C，发布机器C即可。
                 * 2.上次发布A版本对应的机器 A\B,本次发布的A版本对应机器 A,销毁一台机器B即可。
                 * 3.上次发布A版本对应的机器 A\B,本次发布的A版本对应机器 A\C,这种操作页面规避掉。
                 * 4.上次发布A版本对应的机器 A\B,本次发布的B版本对应机器 A\B\C,停掉A版本所有机器，发布B版本所有机器。
                 * 本次处理：停掉上次所有机器，发布本次版本机器
                 */
                List<ListDeployServer> listDeployServers = listDeployServerMapper.selectByDeployid(listDeploy.getId());
                List<String> beforeIpList = listDeployServers.stream().map(ListDeployServer::getServerIp).distinct().collect(Collectors.toList());
                if (StringUtils.equals(publishBo.getPublishType(), PublishConstants.DEPLOY_STATUS.SIMPLE.name())) {
                    log.info("采用100%发布模式，停掉上次发布版本所有机器ip:{} 应用，释放资源", JSON.toJSONString(beforeIpList));
                } else {
                    //List<String> ipList = publishBo.getServerList().stream().map(ServerBo::getIp).distinct().collect(Collectors.toList());
                    //beforeIpList.removeAll(ipList);
                    log.info("采用新老版本兼容模式，停掉上次版本所有机器ip:{} 应用，释放资源", JSON.toJSONString(beforeIpList));
                }

                if (beforeIpList != null) {

                    beforeIpList.forEach(beforeIp->{
                        String res=httpClientTemplate.doGet("http://"+beforeIp+":4789/containers/json?all=true");
                        JSONArray jsonArray=JSON.parseArray(res);
                        JSONObject containersInfo=null;
                        for(Object arry:jsonArray){
                            if(arry.toString().contains(publishBo.getName())){
                                containersInfo= (JSONObject) arry;
                            }
                        }
                        if(containersInfo!=null&&!StringUtils.isBlank(containersInfo.toString())){
                            String id=containersInfo.get("Id").toString();
                            if(containersInfo.get("State").toString().equals("running")){
                                httpClientTemplate.doPost("http://"+beforeIp+":4789/containers/"+id+"/kill",(Object) null);
                            }
                            httpClientTemplate.doDelete("http://"+beforeIp+":4789/containers/"+id);
                        }


                    });
                    //ssh root@172.19.21.226 sh /server/scripts/stop_server.sh 172.19.22.1/point/pointdev:xxxxxxxxxxxxxxxxxxx121212 172.19.21.241 172.19.21.240
//                    String stopShellParam = beforeIpList.stream().collect(Collectors.joining(" "));
//                    String stopShellUrl = "ssh -p 10022 root@" + shellConstants.shellFileIp + " sh " + shellConstants.stopUrl + " " + publishBo.getBeforeRemark() + " " + stopShellParam;
//                    log.warn("调用dev_stop:shell脚本命令：" + stopShellUrl + "");
//                    long start = System.currentTimeMillis();
//                    int stopCode = shellHelper.executeShellReturnexitValue(stopShellUrl);
//                    log.warn("调用dev_stop耗时：{},脚本结果返回：{}", (System.currentTimeMillis() - start) + "/ms", stopCode);
//                    if (stopCode != 13) {//13成功，其它都失败
//                        throw new ServerException("10099", "调用stop:shell停止应用失败，返回code:" + stopCode);
//                    }
                }
            }
        }

        //2.发布版本
        //发布新老版本兼容模式
        VersionBo versionBo = null;
        if (StringUtils.equals(publishBo.getPublishType(), PublishConstants.DEPLOY_STATUS.COMPATIBILITY.name())) {

            //算法计算，发布的版本类型
            versionBo = this.calculatedReboot(publishBo);

            /**
             * 发布版本的场景罗列
             * 1.如果待发布的版本是上次发布的版本，如上次发布版本A-新版本,B-老版本,本次发布是C-新版本,B-老版本
             * 2.如果待发布的版本不是是上次发布的版本，如上次发布版本A-新版本,B-老版本,本次发布是C-新版本,D-老版本
             */
            //新的版本发布
            //ssh root@172.19.21.226 sh /server/scripts/dev_publish.sh 172.19.22.1/point/pointdev:xxxxxxxxxxxxxxxxxxx121212 172.19.21.241 172.19.21.240
           /* String shellUrl = "ssh -p 10022 root@" + shellConstants.shellFileIp + " sh " + shellConstants.publishUrl + " " + publishBo.getRemark() + " " + versionBo.getNewReboots();
            log.warn("调用dev_publish:shell脚本命令发布新版本：" + shellUrl + "");
            int exitCode = shellHelper.executeShellReturnexitValue(shellUrl);
            log.warn("调用dev_publish:shell发布新版本返回脚本结果：" + exitCode + "");
            if (exitCode != 13) {//13成功，其它都失败
                throw new ServerException("10099", "调用shell发布新版本镜像失败,返回code:" + exitCode);
            }

            //老的版本发布
            //ssh root@172.19.21.226 sh /server/scripts/dev_publish.sh 172.19.22.1/point/pointdev:xxxxxxxxxxxxxxxxxxx121212 172.19.21.241 172.19.21.240
            String oldshellUrl = "ssh -p 10022 root@" + shellConstants.shellFileIp + " sh " + shellConstants.publishUrl + " " + publishBo.getBeforeRemark() + " " + versionBo.getOldReboots();
            log.warn("调用dev_publish:shell脚本命令发布老版本：" + oldshellUrl + "");
            int oldexitCode = shellHelper.executeShellReturnexitValue(oldshellUrl);
            log.warn("调用dev_publish:shell发布老版本返回脚本结果：" + oldexitCode + "");
            if (oldexitCode != 13) {//13成功，其它都失败
                throw new ServerException("10099", "调用shell发布老版本镜像失败,返回code:" + oldexitCode);
            }*/
            String remark=harborIp+"/"+publishBo.getName()+"/"+publishBo.getName()+appInfo.getEnv()+":"+publishBo.getGitlabVersion();
            String beforeRemark=publishBo.getBeforeRemark();
            String[] oldRebootList=versionBo.getOldReboots().split(" ");
            String[] newRebootList=versionBo.getNewReboots().split(" ");
            //老
            for(int i=0;i<oldRebootList.length;i++){
                String[] params=beforeRemark.split(":");
                String url=oldRebootList[i];
                //异步拉取镜像 创建
                publishAsync.pullImages(url,params,publishBo.getName(),beforeRemark);
            }
            //新
            for(int i=0;i<newRebootList.length;i++){
                String[] params=remark.split(":");
                String url=newRebootList[i];
                //异步拉取镜像 创建
                publishAsync.pullImages(url,params,publishBo.getName(),remark);
            }


        } else {
            //1.调用shell脚本发布镜像
            String shellParam = publishBo.getServerList().stream().map(ServerBo::getIp).distinct().collect(Collectors.joining(" "));
            String remark=harborIp+"/"+publishBo.getName()+"/"+publishBo.getName()+appInfo.getEnv()+":"+publishBo.getGitlabVersion();
            String[] rebootList=shellParam.split(" ");
            for(int i=0;i<rebootList.length;i++){
                publishAsync.pullImages(rebootList[i], remark.split(":"), publishBo.getName(), remark);

            }
            //ssh root@172.19.21.226 sh /server/scripts/dev_publish.sh 172.19.22.1/point/pointdev:xxxxxxxxxxxxxxxxxxx121212 172.19.21.241 172.19.21.240
            /*String shellUrl = "ssh -p 10022 root@" + shellConstants.shellFileIp + " sh " + shellConstants.publishUrl + " " + publishBo.getBeforeRemark() + " " + shellParam;
            log.warn("调用dev_publish:shell脚本命令：" + shellUrl + "");
            long start = System.currentTimeMillis();
            int exitCode = shellHelper.executeShellReturnexitValue(shellUrl);
            log.warn("调用dev_publish耗时：{},脚本结果返回：{}", (System.currentTimeMillis() - start) + "/ms", exitCode);
            if (exitCode != 13) {//13成功，其它都失败
                throw new ServerException("10099", "调用publish:shell发布镜像失败，返回code：" + exitCode);
            }*/

            publishBo.getServerList().forEach(x -> {
                x.setVagrancy(100);
            });
        }

        //3.创建版本发布记录
        ListDeploy record = new ListDeploy();
        record.setListId(publishBo.getListId());
        record.setBuildId(Integer.parseInt(publishBo.getBuildId() + ""));
        record.setListName(publishBo.getName());
        record.setGitlabVersion(publishBo.getGitlabVersion());
        if (listDeploy != null) {
            record.setBeforeGitlabVersion(listDeploy.getGitlabVersion());
            record.setBeforeVagrancy(listDeploy.getVagrancy());
        }
        record.setVagrancy(publishBo.getVagrancy());
        record.setPublishType(publishBo.getPublishType());
        record.setPublishStatus(PublishConstants.PUBLISH_STATUS.ING.name());
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        //record.setCreateBy(login.getUsername());
        //record.setUpdateBy(login.getUsername());
        listDeployMapper.insertSelective(record);


        //4.创建版本发布服务器记录
        if (versionBo == null) {
            for (ServerBo server : publishBo.getServerList()) {
                ListDeployServer listDeployServer = new ListDeployServer();
                listDeployServer.setDeployId(record.getId());
                listDeployServer.setGitlabVersion(publishBo.getGitlabVersion());
                listDeployServer.setServerIp(server.getIp());
                listDeployServer.setServerPort(server.getPort());
                listDeployServer.setName(server.getName());
                listDeployServer.setVagrancy(publishBo.getVagrancy());
                listDeployServer.setPublishStatus(PublishConstants.PUBLISH_STATUS.ING.name());
                listDeployServer.setCreateTime(new Date());
                listDeployServer.setUpdateTime(new Date());
                //listDeployServer.setCreateBy(login.getUsername());
                //listDeployServer.setUpdateBy(login.getUsername());
                listDeployServerMapper.insertSelective(listDeployServer);
            }
        } else {
            for (ServerBo serverBo : versionBo.getRebootLists()) {
                ListDeployServer listDeployServer = new ListDeployServer();
                listDeployServer.setDeployId(record.getId());
                listDeployServer.setGitlabVersion(serverBo.getGitlabVersion());
                listDeployServer.setServerIp(serverBo.getIp());
                listDeployServer.setServerPort(serverBo.getPort());
                listDeployServer.setName(serverBo.getName());
                listDeployServer.setVagrancy(serverBo.getVagrancy());
                listDeployServer.setPublishStatus(PublishConstants.PUBLISH_STATUS.ING.name());
                listDeployServer.setCreateTime(new Date());
                listDeployServer.setUpdateTime(new Date());
                //listDeployServer.setCreateBy(login.getUsername());
                //listDeployServer.setUpdateBy(login.getUsername());
                listDeployServerMapper.insertSelective(listDeployServer);
            }
        }

        //5.更新流量占比 100%
        PublishList publishList = new PublishList();
        publishList.setId(publishBo.getListId());
        publishList.setNum(publishBo.getServerList().size());
        publishList.setUpdateTime(new Date());
        publishListMapper.updateByPrimaryKeySelective(publishList);
        return record.getId();


    }

    /**
     * 检查发布
     *
     * @param publishBo
     */
//    @Override
//    public String checkDeploy(PublishBo publishBo) {
//
//        //查询对象
//        ListDeploy listDeploy = listDeployMapper.selectById(publishBo.getDeployId());
//        if (listDeploy == null) {
//            throw new ServerException("10099", "deployid:" + publishBo.getDeployId() + ",发布记录不存在!");
//        }
//
//        //查询发布详情记录
//        List<ListDeployServer> sourceIpList = listDeployServerMapper.selectByDeployid(publishBo.getDeployId());
//        if (CollectionUtils.isEmpty(sourceIpList)) {
//            throw new ServerException("10099", "deployid:" + publishBo.getDeployId() + "，发布详情记录不存在");
//        }
//
//        //获取脚本地址
//        ListBuild listBuild = listBuildMapper.selectByPrimaryKey(new Long(listDeploy.getBuildId()));
//        if (listBuild == null) {
//            throw new ServerException("10099", "buildid:" + listDeploy.getBuildId() + "，编译详情记录不存在");
//        }
//
//        PublishList publishLists = publishListMapper.selectByPrimaryKey(listBuild.getListId());
//        if (publishLists == null) {
//            throw new ServerException("10099", "项目信息不存在!");
//        }
//
//        /**
//         * 如果不是定时任务调用,先检查数据库的状态
//         */
//        if (!StringUtils.equals(listDeploy.getPublishStatus(), PublishConstants.PUBLISH_STATUS.ING.name())) {
//            log.info("eployId：{},db状态:{},jobs:{}", listDeploy.getId(), listDeploy.getPublishStatus(), publishBo.getIsJob());
//            return listDeploy.getPublishStatus();
//        }
//
//        //1.调用shell脚本发布镜像
//        String shellParam = sourceIpList.stream().map(ListDeployServer::getServerIp).distinct().collect(Collectors.joining(" "));
//        String[] urlList=shellParam.split(" ");
//        String deployStatus = PublishConstants.PUBLISH_STATUS.SUCCESS.name();
//        List<ShellBo> shellBoList = new ArrayList<>();
//        List<Future<ShellBo>> futures=new ArrayList<>();
//        for (int i=0;i<urlList.length;i++){
//            Future<ShellBo> shellBoFuture = publishAsync.verifyDeploy(urlList[i], listDeploy.getListName());
//            futures.add(shellBoFuture);
//        }
//        shellBoList=FeatureUtils.getFeaturesResult(futures,log);
//        for(ShellBo shellBo:shellBoList) {
//            if(shellBo.getResult().equals("FAIL") || Collections.isEmpty(shellBoList)){
//                deployStatus=PublishConstants.PUBLISH_STATUS.FAIL.name();
//            }
//        }
//        int loopTime = listDeploy.getLooping();
//        if(deployStatus.equals(PublishConstants.PUBLISH_STATUS.FAIL.name()) && loopTime < 6) {
//            deployStatus = PublishConstants.PUBLISH_STATUS.ING.name();
//        }
//        //更改发布详细记录状态
//        log.info("检查dev_check:shell返回服务器列表数据格式正常，执行更新操作");
//        for (ListDeployServer sourceIp : sourceIpList) {
//            for (ShellBo shellBo : shellBoList) {
//                if (sourceIp.getServerIp().equals(shellBo.getIp())) {
//                    shellBo.setId(sourceIp.getId());
//                    listDeployServerMapper.updateByIps(shellBo);
//                }
//            }
//        }
//
//        //发布都成功，才统一设置Kong
//        if (PublishConstants.APPLICATION_STATUS.server_outer.name().equals(publishLists.getType()) && deployStatus.equals(PublishConstants.PUBLISH_STATUS.SUCCESS.name())) {
//            publishBo.setName(publishLists.getName());
//            List<ServerBo> serverList = new ArrayList<>();
//            for (ListDeployServer serverDeployList : sourceIpList) {
//                ServerBo serverBo = new ServerBo();
//                serverBo.setVagrancy(serverDeployList.getVagrancy());
//                serverBo.setPort(serverDeployList.getServerPort());
//                serverBo.setIp(serverDeployList.getServerIp());
//                serverList.add(serverBo);
//            }
//            publishBo.setServerList(serverList);
//            publishBo.setPublishType(listDeploy.getPublishType());
//            publishBo.setGatewayType(publishLists.getRouteType());
//            publishAsync.executeKong(publishBo);
//        }
//
//        //更改发布记录状态
//        ListDeploy record = new ListDeploy();
//        record.setId(publishBo.getDeployId());
//        record.setPublishStatus(deployStatus);
//        record.setUpdateTime(new Date());
//        record.setLooping(loopTime + 1);
//        listDeployMapper.updateByPrimaryKeySelective(record);
//        return deployStatus;
//    }

    /**
     * 检查发布
     *
     * @param publishBo
     */
    @Override
    public String checkDeploy(PublishBo publishBo) {

        //查询对象
        ListDeploy listDeploy = listDeployMapper.selectById(publishBo.getDeployId());
        if (listDeploy == null) {
            throw new ServerException("10099", "deployid:" + publishBo.getDeployId() + ",发布记录不存在!");
        }

        //查询发布详情记录
        List<ListDeployServer> sourceIpList = listDeployServerMapper.selectByDeployid(publishBo.getDeployId());
        if (CollectionUtils.isEmpty(sourceIpList)) {
            throw new ServerException("10099", "deployid:" + publishBo.getDeployId() + "，发布详情记录不存在");
        }

        //获取脚本地址
        ListBuild listBuild = listBuildMapper.selectByPrimaryKey(new Long(listDeploy.getBuildId()));
        if (listBuild == null) {
            throw new ServerException("10099", "buildid:" + listDeploy.getBuildId() + "，编译详情记录不存在");
        }

        PublishList publishLists = publishListMapper.selectByPrimaryKey(listBuild.getListId());
        if (publishLists == null) {
            throw new ServerException("10099", "项目信息不存在!");
        }

        /**
         * 如果不是定时任务调用,先检查数据库的状态
         */
        if (!StringUtils.equals(listDeploy.getPublishStatus(), PublishConstants.PUBLISH_STATUS.ING.name())) {
            log.info("eployId：{},db状态:{},jobs:{}", listDeploy.getId(), listDeploy.getPublishStatus(), publishBo.getIsJob());
            return listDeploy.getPublishStatus();
        }

        //1.调用shell脚本发布镜像
        String shellParam = sourceIpList.stream().map(ListDeployServer::getServerIp).distinct().collect(Collectors.joining(" "));
        String[] urlList=shellParam.split(" ");
        String deployStatus = PublishConstants.PUBLISH_STATUS.SUCCESS.name();
        List<ShellBo> shellBoList = new ArrayList<>();
        for (int i = 0 ; i < urlList.length; i++){
            ShellBo onceResult = publishAsync.syncVerifyDeploy(urlList[i], listDeploy.getListName());
            shellBoList.add(onceResult);
        }

        for(ShellBo shellBo: shellBoList) {
            if(shellBo.getResult().equals("FAIL") || Collections.isEmpty(shellBoList)){
                deployStatus=PublishConstants.PUBLISH_STATUS.FAIL.name();
            }
        }

        int loopTime = listDeploy.getLooping();
        if(deployStatus.equals(PublishConstants.PUBLISH_STATUS.FAIL.name()) && loopTime <= configs.getDeployCheckHealthTimes() ) {
            deployStatus = PublishConstants.PUBLISH_STATUS.ING.name();
        }
        //更改发布详细记录状态
        log.info("检查dev_check:shell返回服务器列表数据格式正常，执行更新操作");
        for (ListDeployServer sourceIp : sourceIpList) {
            for (ShellBo shellBo : shellBoList) {
                if (sourceIp.getServerIp().equals(shellBo.getIp())) {
                    shellBo.setId(sourceIp.getId());
                    listDeployServerMapper.updateByIps(shellBo);
                }
            }
        }

        //发布都成功，才统一设置Kong
        if (PublishConstants.APPLICATION_STATUS.server_outer.name().equals(publishLists.getType()) && deployStatus.equals(PublishConstants.PUBLISH_STATUS.SUCCESS.name())) {
            publishBo.setName(publishLists.getName());
            List<ServerBo> serverList = new ArrayList<>();
            for (ListDeployServer serverDeployList : sourceIpList) {
                ServerBo serverBo = new ServerBo();
                serverBo.setVagrancy(serverDeployList.getVagrancy());
                serverBo.setPort(serverDeployList.getServerPort());
                serverBo.setIp(serverDeployList.getServerIp());
                serverList.add(serverBo);
            }
            publishBo.setServerList(serverList);
            publishBo.setPublishType(listDeploy.getPublishType());
            publishBo.setGatewayType(publishLists.getRouteType());
            publishAsync.executeKong(publishBo);
        }

        //更改发布记录状态
        ListDeploy record = new ListDeploy();
        record.setId(publishBo.getDeployId());
        record.setPublishStatus(deployStatus);
        record.setUpdateTime(new Date());
        listDeployMapper.updateByPrimaryKeySelective(record);
        return deployStatus;
    }


    /**
     * @param publishBo 设置需要发布的机器集合权重
     */
    public void calculatePublishServerPre(ServerBo server, PublishBo publishBo) {

        if (publishBo.getServerList().size() == 1 && StringUtils.equals(publishBo.getPublishType(), PublishConstants.DEPLOY_STATUS.SIMPLE.name())) {
            server.setVagrancy(100);
        } else if (publishBo.getServerList().size() >= 2 && StringUtils.equals(publishBo.getPublishType(), PublishConstants.DEPLOY_STATUS.SIMPLE.name())) {
            server.setVagrancy(100);
        } else if (StringUtils.equals(publishBo.getPublishType(), PublishConstants.DEPLOY_STATUS.COMPATIBILITY.name())) {
            if (publishBo.getVagrancy() == 1000) {
                server.setVagrancy(publishBo.getVagrancy() / 10);
            } else {
                //根据机器数量平摊比例
                Integer avgVagrancy = publishBo.getVagrancy() / publishBo.getServerList().size();
                server.setVagrancy(avgVagrancy);
            }
        }
    }

    @Override
    public PublishList initCheckDeploy(PublishBo publishBo) {

        PublishList publishLists = publishListMapper.selectByPrimaryKey(publishBo.getId());
        if (publishLists == null) {
            throw new ServerException("10099", "项目信息不存在!");
        }

        //获取最新编译记录
        ListBuild listBuild = listBuildMapper.selectByPrimaryKey(publishBo.getBuildId());
        if (listBuild == null) {
            throw new ServerException("10099", "构建信息不存在!");
        }

        if (!StringUtils.equals(listBuild.getBuildStatus(), PublishConstants.BUILD_STATUS.SUCCESS.name())) {
            throw new ServerException("10099", "构建成功后才能发布！");
        }

        if (StringUtils.equals(publishBo.getPublishType(), PublishConstants.DEPLOY_STATUS.COMPATIBILITY.name())) {

            if (publishBo.getVagrancy() >= 100) {
                throw new ServerException("10099", "版本占比不能超100");
            }

            if (publishBo.getVagrancy() % 10 != 0) {
                throw new ServerException("10099", "流量占比只能支持10的倍数！");
            }

            if (StringUtils.isBlank(publishBo.getBeforeGitlabVersion())) {
                throw new ServerException("10099", "次更新版本不能为空！");
            }

            if (publishBo.getServerList().size() <= 1) {
                throw new ServerException("10099", "新老版本兼容模式，发布机器要超过1台");
            }

            ListBuild listBuilds = listBuildMapper.selectByHarborKey(publishBo.getBeforeGitlabVersion());
            if (listBuilds == null || listBuilds.getRemark() == null) {
                throw new ServerException("10099", "beforeGitlabVersion错误");
            }
            ListBuild lBuild = listBuildMapper.selectByHarborKey(publishBo.getGitlabVersion());
            if (lBuild == null || lBuild.getRemark() == null) {
                throw new ServerException("10099", "gitlabVersion错误");
            }

            publishBo.setBeforeRemark(listBuilds.getRemark());
            publishBo.setRemark(lBuild.getRemark());
        } else {

            ListBuild record = listBuildMapper.selectByHarborKey(publishBo.getGitlabVersion());
            if (record == null || record.getRemark() == null) {
                throw new ServerException("10099", "gitlabversion错误");
            }
            publishBo.setBeforeRemark(record.getRemark());
        }
        return publishLists;

    }


    /**
     * 计算机器分配
     *
     * @param publishBo
     */
    public VersionBo calculatedReboot(PublishBo publishBo) {

        //占比
        Integer vagrancy = publishBo.getVagrancy();

        //机器列表
        List<ServerBo> serverList = publishBo.getServerList();

        //待发布的机器数量
        int rebootNum = serverList.size();

        //Tomcat默认线程数
        //int tomcatThreadCount = 200;

        //系统默认1s处理1000个请求
        //int tps = 1000;

        //float s = tps / tomcatThreadCount;
        //int needRebootNum = Math.round(s);


        int oldVersionRebootNum = 0;
        int newVersionRebootNum = 0;

        BigDecimal b = new BigDecimal(vagrancy);
        b = b.divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP);
        BigDecimal vagrancyResutl = new BigDecimal(rebootNum);
        vagrancyResutl = vagrancyResutl.multiply(b);

        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(vagrancyResutl.toString().replace(".0", ""));
        if (m.matches()) {
            newVersionRebootNum = vagrancyResutl.intValue();
            oldVersionRebootNum = rebootNum - newVersionRebootNum;
        } else {
            newVersionRebootNum = Math.round(vagrancyResutl.floatValue()) == 0 ? 1 : Math.round(vagrancyResutl.floatValue());
            if (newVersionRebootNum == rebootNum) {
                newVersionRebootNum--;
            }
            if (newVersionRebootNum == 0) {
                newVersionRebootNum++;
            }
            oldVersionRebootNum = rebootNum - newVersionRebootNum;
        }

        //获取新老版本机器数量
        List<ServerBo> oldServerBos = BeanUtils.getSubList(serverList, 0, oldVersionRebootNum);
        List<ServerBo> newServerBos = BeanUtils.getSubList(serverList, oldVersionRebootNum, serverList.size());
        String oldIps = oldServerBos.stream().map(ServerBo::getIp).distinct().collect(Collectors.joining(" "));
        String newIps = newServerBos.stream().map(ServerBo::getIp).distinct().collect(Collectors.joining(" "));

        //构建版本对象
        VersionBo versionBo = new VersionBo();
        versionBo.setNewVersionRebootNum(newVersionRebootNum);
        versionBo.setOldVersionRebootNum(oldVersionRebootNum);
        versionBo.setOldReboots(oldIps);
        versionBo.setNewReboots(newIps);


        List<ServerBo> result = new ArrayList<>();
        //Kong的流量占比
        int oldVagrancy = 0;
        int newVagrancy = 0;
        /*if (needRebootNum < rebootNum) {//如果计算机器数量大于待发布机器数量，Kong采用均衡算法
            newVagrancy = 100;
            oldVagrancy = 100;
        } else {*/
        float newFloat = (float) (vagrancy * 10) / newVersionRebootNum;
        float oldFloat = (float) (1000 - (vagrancy * 10)) / oldVersionRebootNum;

        newVagrancy = Math.round(newFloat);
        oldVagrancy = Math.round(oldFloat);
        /**
         * Kong是100的整数，计算处理
         * //优先乘以2是否整除100
         */
        if (newVagrancy % 100 != 0) {
            log.info("<calculated>计算前新版本/Kong流量占比:{}", newVagrancy);
            int remainder = newVagrancy % 100;
            int remachCount = 100 - remainder;
            newVagrancy = newVagrancy + remachCount;
            log.info("<calculated>计算后新版本/Kong流量占比:{}", newVagrancy);
        }

        if (oldVagrancy % 100 != 0) {
            log.info("<calculated>计算前老版本/Kong流量占比:{}", oldVagrancy);
            int oldRemainder = oldVagrancy % 100;
            int oldremachCount = 100 - oldRemainder;
            oldVagrancy = oldVagrancy + oldremachCount;
            log.info("<calculated>计算后老版本/Kong流量占比:{}", oldVagrancy);
        }

        //}

        int finalOldVagrancy = oldVagrancy;
        int finalNewVagrancy = newVagrancy;
        oldServerBos.forEach(x -> {
            x.setVagrancy(finalOldVagrancy);
            x.setGitlabVersion(publishBo.getBeforeGitlabVersion());
        });
        newServerBos.forEach(y -> {
            y.setVagrancy(finalNewVagrancy);
            y.setGitlabVersion(publishBo.getGitlabVersion());
        });

        result.addAll(oldServerBos);
        result.addAll(newServerBos);
        publishBo.setServerList(result);
        //fabu
        versionBo.setOldRebootLists(oldServerBos);
        versionBo.setNewRebootLists(newServerBos);
        versionBo.setRebootLists(result);
        return versionBo;
    }

}
