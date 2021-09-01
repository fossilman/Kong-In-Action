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
public class Login implements Serializable {
    /**
     *
     */
    @JsonIgnore
    private Long id;

    /**
     * 用户名
     */
    @JsonIgnore
    private String name;

    /**
     * 密码
     */
    @JsonIgnore
    private String password;

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
     * kong 状态
     */
    private Boolean kongStatus;

    /**
     * token
     */
    private String token;

    private String username;
    private Integer node;

    /**
     * pb_login
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
        sb.append(", password=").append(password);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

}
