package model;


import org.fibonacci.devopscenter.domain.ListBuild;
import org.fibonacci.devopscenter.domain.ListDeployServer;
import org.fibonacci.devopscenter.domain.Server;
import lombok.Data;

import java.util.List;

/**
 * @author krame
 * @description：服务器对象
 * @date ：Created in 2019-09-17 16:26
 */
@Data
public class ServerVo {


    private List<Server> serverList;

    private List<ListDeployServer> beforeServers;

    private Integer total;

    private Long id;

    private List<ListBuild> listBuilds;
}
