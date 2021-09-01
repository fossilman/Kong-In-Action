package org.fibonacci.devopscenter.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
  * @author krame
  * @description：edu.mapper
  * @date ：Created in 2019-09-17 07:56
 */
@Data
public class Server implements Serializable {
    /**
     *
     */
    private Long id;

    /**
     * 服务器名称
     */
    private String name;

    /**
     * 服务器ip
     */
    private String ip;

    /**
     * 服务器端口
     */
    private String port;

    /**
     * 描述
     */
    private String remark;

    /**
     * 组
     */
    private String team;


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
     * pd_server
     */
    private static final long serialVersionUID = 1L;


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", ip=").append(ip);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
