package org.fibonacci.devopscenter.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author krame
 * @description：edu.mapper
 * @date ：Created in 2019-09-17 07:56
 */
@Data
public class PublishList implements Serializable {
    /**
     *
     */
    private Long id;

    /**
     * 应用名称
     */
    private String name;

    /**
     * 机器数量
     */
    private Integer num;


    /**
     * gitlabid
     */
    private Integer gitlabId;


    /**
     * 备注
     */
    private String remark;


    /**
     *
     */
    private Integer del;

    /**
     * 创建时间
     */
    @JsonIgnore
    private Date createTime;

    /**
     * 类型
     */
    private String type;
    private String kind;

    private String createBy;

    private String groups;

    //路由策略
    private String routeType;

    /**
     * 更新时间
     */
    @JsonIgnore
    private Date updateTime;

    private List<ListDeploy> listDeploys;

    private List<ListBuild> listBuilds;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", num=").append(num);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }


}
