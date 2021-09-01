package org.fibonacci.routeplus.domain;

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
public class Plugin implements Serializable {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 插件名称
     */
    private String pluginName;

    /**
     * 备注
     */
    private String remark;

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

    /**
     * plugin
     */
    private static final long serialVersionUID = 1L;



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", pluginName=").append(pluginName);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}
