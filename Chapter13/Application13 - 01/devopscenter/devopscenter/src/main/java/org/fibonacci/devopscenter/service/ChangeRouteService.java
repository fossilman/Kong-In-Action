package org.fibonacci.devopscenter.service;

import com.google.common.base.Preconditions;
import org.fibonacci.devopscenter.constants.PublishConstants;
import org.fibonacci.devopscenter.domain.ListDeploy;
import org.fibonacci.devopscenter.domain.ListDeployServer;
import org.fibonacci.devopscenter.domain.PublishList;
import org.fibonacci.devopscenter.domain.Server;
import org.fibonacci.devopscenter.mapper.ListDeployMapper;
import org.fibonacci.devopscenter.mapper.ListDeployServerMapper;
import org.fibonacci.devopscenter.mapper.PublishListMapper;
import org.fibonacci.devopscenter.mapper.ServerMapper;
import org.fibonacci.framework.exceptions.ServerException;
import org.fibonacci.routeplus.common.bo.RoutePlusBo;
import org.fibonacci.routeplus.rpc.feign.RouteFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ChangeRouteService {


    @Autowired
    private RouteFeignClient routeFeignClient;

    @Autowired
    private PublishListMapper publishListMapper;
    @Autowired
    private ListDeployMapper listDeployMapper;
    @Autowired
    private ListDeployServerMapper deployServerMapper;
    @Autowired
    private ServerMapper serverMapper;

    @Transactional
    public void changeVagrancy(Long appId, Integer vagrancy) {

        PublishList appInfo = publishListMapper.selectByPrimaryKey(appId);
        Preconditions.checkArgument(appInfo != null, "应用 %s 不存在", appId );
        Preconditions.checkArgument(PublishConstants.APPLICATION_STATUS.server_outer.name().equalsIgnoreCase(appInfo.getType()), "应用类型 %s 不支持配置流量", appInfo.getType());

        ListDeploy deployRecord = listDeployMapper.selectLastOneByListId(appId);
        Preconditions.checkArgument(deployRecord != null, "还没有任何发布记录， 不能切流量");

        Long deployId = deployRecord.getId();

        List<ListDeployServer> deployServers = deployServerMapper.selectByDeployid(deployId);
        Preconditions.checkArgument(!deployServers.isEmpty(), "找不到运行中的服务器列表");

        if (vagrancy >= 100) {
            throw new ServerException("10099", "版本占比不能超100");
        }

        if (vagrancy % 10 != 0) {
            throw new ServerException("10099", "流量占比只能支持10的倍数！");
        }

        if (deployServers.size() != 2) {
            throw new ServerException("10099", "本例中发布机器要正好2台哦");
        }

        List<Server> list = serverMapper.listByTeam(appInfo.getName());
        if (list.size() != 2) {
            throw new ServerException("10099", "本例中发布机器要正好2台哦");
        }

        List<RoutePlusBo.ServerBo> serverList = new ArrayList<>();

        ListDeployServer server1 = deployServers.stream().filter(it -> Objects.equals(it.getServerIp(),list.get(0).getIp())).findFirst().get();
        RoutePlusBo.ServerBo serverBo1 = new RoutePlusBo.ServerBo();
        serverBo1.setIp(server1.getServerIp());
        serverBo1.setPort(server1.getServerPort());
        int s1_vagrancy = vagrancy * 10;
        serverBo1.setVagrancy(s1_vagrancy);
        serverList.add(serverBo1);


        ListDeployServer server2 = deployServers.stream().filter(it -> Objects.equals(it.getServerIp(),list.get(1).getIp())).findFirst().get();
        RoutePlusBo.ServerBo serverBo2 = new RoutePlusBo.ServerBo();
        serverBo2.setIp(server2.getServerIp());
        serverBo2.setPort(server2.getServerPort());
        int s2_vagrancy = 1000 - vagrancy * 10;
        serverBo2.setVagrancy(s2_vagrancy);
        serverList.add(serverBo2);


        RoutePlusBo routePlusBo = new RoutePlusBo();
        routePlusBo.setGatewayType(appInfo.getType());
        routePlusBo.setName(appInfo.getName());
        routePlusBo.setPublishType(deployRecord.getPublishType());
        routePlusBo.setServerList(serverList);
        routeFeignClient.deployKong(routePlusBo);



        ListDeploy updateDeploy = new ListDeploy();
        updateDeploy.setId(deployRecord.getId());
        updateDeploy.setBeforeVagrancy(deployRecord.getVagrancy());
        updateDeploy.setVagrancy(vagrancy);
        listDeployMapper.updateByPrimaryKeySelective(updateDeploy);
        ListDeployServer updateS1 = new ListDeployServer();
        updateS1.setId(server1.getId());
        updateS1.setVagrancy(s1_vagrancy);
        deployServerMapper.updateByPrimaryKeySelective(updateS1);
        ListDeployServer updateS2 = new ListDeployServer();
        updateS2.setId(server2.getId());
        updateS2.setVagrancy(s2_vagrancy);
        deployServerMapper.updateByPrimaryKeySelective(updateS2);



    }
}
