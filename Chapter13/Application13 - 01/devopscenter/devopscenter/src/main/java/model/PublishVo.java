package model;


import org.fibonacci.devopscenter.domain.ListBuild;
import org.fibonacci.devopscenter.domain.ListDeploy;
import org.fibonacci.devopscenter.domain.ListDeployServer;
import org.fibonacci.devopscenter.domain.PublishList;
//import edu.jiahui.routeplus.common.bo.TargetBo;
import lombok.Data;

import java.util.List;

/**
 * @author krame
 * @description：发布对象
 * @date ：Created in 2019-09-17 16:26
 */
@Data
public class PublishVo {


    private List<PublishList> publishList;

    private List<ListBuild> buildList;
    private List<ListDeploy> listDeploys;

    private Long id;
    private Long deployId;
    private String ip;
    private String status;
    private String harborNo;
    private String remark;
    private String remark1;

    private List<ListDeployServer> newDeployServerList;
    private List<ListDeployServer> oldDeployServerList;
    //private List<TargetBo> targets;



    /**
     * jenkins-jobid
     */
    private Integer jobId;

    /**
     * 构建状态 (ING/SUCCESS/FAILURE)
     */
    private String buildStatus;


    private Integer total;

    /**
     * 构建状态 (ING/SUCCESS/FAILURE)
     */
    private String deployStatus;


    /**
     * 发布服务器id
     */
    private List<Long> listServerId;

    private String gatewayType;
    /**
     * 类型
     */
    private String type;
    /**
     * 应用名称
     */
    private String name;


}
