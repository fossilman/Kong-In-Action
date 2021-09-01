package org.fibonacci.devopscenter.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
  * @author krame
  * @description：发布编译
  * @date ：Created in 2019-10-21 02:22
 */
@Data
public class ListDeploy implements Serializable {
    /**
     *
     */
    private Long id;

    /**
     * 发布id
     */
    private Long listId;

    /**
     * 编译id
     */
    private Integer buildId;

    /**
     * 应用名称
     */
    private String listName;

    /**
     * 新版本编号
     */
    private String gitlabVersion;

    /**
     * 上次版本编号
     */
    private String beforeGitlabVersion;

    /**
     * 新版本占比
     */
    private Integer vagrancy;

    /**
     * 上次版本占比
     */
    @JsonIgnore
    private Integer beforeVagrancy;

    /**
     * 发布类型
     */
    private String publishType;

    /**
     * 发布状态
     */
    private String publishStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonIgnore
    private Date updateTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 轮训次数
     */
    private Integer looping;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * pb_list_deploy
     */
    private static final long serialVersionUID = 1L;


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", listId=").append(listId);
        sb.append(", buildId=").append(buildId);
        sb.append(", listName=").append(listName);
        sb.append(", gitlabVersion=").append(gitlabVersion);
        sb.append(", beforeGitlabVersion=").append(beforeGitlabVersion);
        sb.append(", vagrancy=").append(vagrancy);
        sb.append(", beforeVagrancy=").append(beforeVagrancy);
        sb.append(", publishType=").append(publishType);
        sb.append(", publishStatus=").append(publishStatus);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", createBy=").append(createBy);
        sb.append(", updateBy=").append(updateBy);
        sb.append("]");
        return sb.toString();
    }
}
