package org.fibonacci.routeplus.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.fibonacci.routeplus.model.RouteVo;
import org.fibonacci.routeplus.model.bo.BaseBo;
import org.fibonacci.routeplus.model.bo.ServicesRoutesBo;
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
 * @date ：Created in 2020-05-07 04:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationServices extends BaseBo implements Serializable {
    /**
     * 主键
     */
    @JsonProperty("serviceId")
    private Integer id;

    /**
     * 应用id
     */
    @JsonIgnore
    private Integer applicationId;

    /**
     * 应用名称
     */
    @JsonIgnore
    private String applicationName;

    /**
     *
     */
    private String kongServicesId;

    /**
     *
     */
    private String kongServicesName;

    /**
     * 内部路径
     */
    @NotNull
    private String path;

    /**
     * 是否开启
     */
    private Boolean enabled;

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


    private List<ServicesRoutes> routesPool;
    private List<RouteVo.Plugins> plugins;
    private List<ServicesRoutesBo> routes;



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", applicationId=").append(applicationId);
        sb.append(", applicationName=").append(applicationName);
        sb.append(", path=").append(path);
        sb.append(", enabled=").append(enabled);
        sb.append(", remark=").append(remark);
        sb.append(", createBy=").append(createBy);
        sb.append(", updateBy=").append(updateBy);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}
