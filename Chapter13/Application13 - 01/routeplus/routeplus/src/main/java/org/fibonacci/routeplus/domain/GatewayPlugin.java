package org.fibonacci.routeplus.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.fibonacci.routeplus.utils.DateToLong;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
  * @author ：zachary
  * @description：org.fibonacci.routeplus.mapper
  * @date ：Created in 2020-05-08 01:49
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GatewayPlugin implements Serializable {
    /**
     * 主键
     */
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
     * 网关/应用(gateway/application)
     */
    private String name;

    /**
     * pc/openapi/web
     */
    private String type;

    /**
     * 插件id
     */
    private Integer pluginId;

    /**
     * 插件名称
     */
    private String pluginName;

    /**
     * 是否开启
     */
    private Boolean enabled;

    /**
     * 限流json{"id","dwcwecwecwecwecw","quantity":10,"period":"second","enabled":true}
     */
    private String pluginConfig;

    /**
     * 创建时间
     */
    @JsonSerialize(using = DateToLong.class)
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonSerialize(using = DateToLong.class)
    private Date updateTime;



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", type=").append(type);
        sb.append(", pluginId=").append(pluginId);
        sb.append(", pluginName=").append(pluginName);
        sb.append(", rateLimiting=").append(pluginConfig);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}
