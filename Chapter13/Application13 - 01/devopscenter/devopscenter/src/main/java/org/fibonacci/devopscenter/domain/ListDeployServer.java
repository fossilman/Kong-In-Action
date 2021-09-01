package org.fibonacci.devopscenter.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
  * @author krame
  * @description：发布编译详情
  * @date ：Created in 2019-10-21 02:22
 */
@Data
public class ListDeployServer implements Serializable {
    /**
     *
     */
    private Long id;

    /**
     * deploy_id
     */
    private Long deployId;

    /**
     * 上次版本编号
     */
    private String gitlabVersion;

    /**
     * 服务器别名
     */
    private String name;

    /**
     * 服务器ip
     */
    private String serverIp;

    /**
     * 服务器端口
     */
    private String serverPort;

    /**
     * 流量占比
     */
    private Integer vagrancy;

    /**
     * 发布状态
     */
    private String publishStatus;

    /**
     * 创建时间
     */
    @JsonIgnore
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonIgnore
    private Date updateTime;

    /**
     * 创建人
     */
    @JsonIgnore
    private String createBy;

    /**
     * 修改人
     */
    @JsonIgnore
    private String updateBy;

    /**
     * pb_list_deploy_server
     */
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", deployId=").append(deployId);
        sb.append(", gitlabVersion=").append(gitlabVersion);
        sb.append(", serverIp=").append(serverIp);
        sb.append(", serverPort=").append(serverPort);
        sb.append(", vagrancy=").append(vagrancy);
        sb.append(", publishStatus=").append(publishStatus);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", createBy=").append(createBy);
        sb.append(", updateBy=").append(updateBy);
        sb.append("]");
        return sb.toString();
    }
}
