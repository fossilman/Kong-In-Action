package org.fibonacci.devopscenter.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
  * @author krame
  * @description：edu.mapper
  * @date ：Created in 2019-09-23 02:44
 */
@Data
public class ListBuild extends Base implements Serializable {
    /**
     *
     */
    private Long id;

    /**
     * 发布id
     */
    private Long listId;

    /**
     * gitlab的主键
     */
    @JsonIgnore
    private Integer gitlabId;

    /**
     * gitlab版本号
     */
    private String gitlabVersion;

    /**
     * gitlab头部
     */
    private String gitlabHead;

    /**
     * gitlab描述
     */
    private String gitlabDesc;

    /**
     * gitlab作则
     */
    private String pushAuthor;

    /**
     * 编译状态
     */
    private String buildStatus;

    /**
     * jekins的job-id
     */
    @JsonIgnore
    private Integer jenkinsId;

    /**
     * jekins的job-id
     */
    private String harborKey;

    /**
     * 流量占比
     */
    private Integer vagrancy;

    /**
     * 备注
     */
    private String remark;

    /**
     * gitlab发布时间
     */
    private Date gitlabTime;

    /**
     * gitlab发布时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonIgnore
    private Date updateTime;

    /**
     * 失败原因
     */
    private String failReason;
    /**
     * pb_list_build
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
        sb.append(", gitlabId=").append(gitlabId);
        sb.append(", gitlabVersion=").append(gitlabVersion);
        sb.append(", gitlabHead=").append(gitlabHead);
        sb.append(", gitlabDesc=").append(gitlabDesc);
        sb.append(", pushAuthor=").append(pushAuthor);
        sb.append(", buildStatus=").append(buildStatus);
        sb.append(", jenkinsId=").append(jenkinsId);
        sb.append(", vagrancy=").append(vagrancy);
        sb.append(", remark=").append(remark);
        sb.append(", gitlabTime=").append(gitlabTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
