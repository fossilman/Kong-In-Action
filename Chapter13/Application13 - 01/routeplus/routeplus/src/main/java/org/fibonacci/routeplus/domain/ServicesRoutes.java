package org.fibonacci.routeplus.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.fibonacci.routeplus.model.RouteVo;
import org.fibonacci.routeplus.utils.DateToLong;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
  * @author ：zachary
  * @description：org.fibonacci.routeplus.mapper
  * @date ：Created in 2020-05-08 01:49
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServicesRoutes implements Serializable {
    /**
     * 主键
     */
    @JsonProperty("routeId")
    private Integer id;

    /**
     * 应用层主键id
     */
    @JsonIgnore
    private Integer serviceId;

    /**
     * 路由类型(pc/web/openapi)
     */
    @NotNull
    private String routeType;

    /**
     * 内部路径
     */
    private String innerPath;

    /**
     * 外部路径
     */
    private String outPath;

    /**
     * 是否开启
     */
    private Boolean enabled;

    /**
     *
     */
    private String kongRoutesId;

    /**
     *
     */
    private String kongRoutesName;


    /**
     * 描述
     */
    private String remark;

    /**
     * 创建人
     */
    @JsonIgnore
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 创建时间
     */
    @JsonIgnore
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonSerialize(using = DateToLong.class)
    private Date updateTime;
    private List<RouteVo.Plugins> plugins;


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", serviceId=").append(serviceId);
        sb.append(", routeType=").append(routeType);
        sb.append(", innerPath=").append(innerPath);
        sb.append(", outPath=").append(outPath);
        sb.append(", enabled=").append(enabled);
        sb.append(", kongRoutesId=").append(kongRoutesId);
        sb.append(", kongRoutesName=").append(kongRoutesName);
        sb.append(", remark=").append(remark);
        sb.append(", createBy=").append(createBy);
        sb.append(", updateBy=").append(updateBy);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}
