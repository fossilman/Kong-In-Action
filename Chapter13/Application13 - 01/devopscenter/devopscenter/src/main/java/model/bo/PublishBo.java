package model.bo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author krame
 * @description：发布对象
 * @date ：Created in 2019-09-17 16:26
 */
@Data
public class PublishBo extends BaseBo {

    @NotBlank(message = "应用名称不能为空", groups = {savePublishGroup.class})
    private String applicationName;

    private String type;
    private String kind;

    @NotNull(message = "服务器列表不能为空", groups = {deployPublishGroup.class})
    private List<ServerBo> serverList;

    private Integer ipNum;


    /**
     * 流量占比，默认10%，对应Kong
     */
    private Integer vagrancy = 100;

    /**
     * 发布id
     */
    @NotNull(message = "发布id不能为空", groups = {updatePublishGroup.class, buildPublishGroup.class, deployPublishGroup.class})
    private Long id;

    /**
     * deployid
     */
    private Long deployId;
    /**
     * 最新版本编译id
     */
    @NotNull(message = "编译id不能为空", groups = {deployPublishGroup.class})
    private Long buildId;

    /**
     * 次新版本编译id
     */
    private Long beforeBuildId;

    /**
     * 次新版本gitlab对应版本号
     */
    private String beforeGitlabVersion;

    /**
     * 应用名称
     */
    private String name;


    private Long listId;


    private Long publishId;


    /**
     * gitlab对应id
     */
    private Integer gitlabId;

    /**
     * gitlab对应版本号
     */
    @NotEmpty(message = "gitlabVersion不能为空", groups = {buildPublishGroup.class, deployPublishGroup.class})
    private String gitlabVersion;

    /**
     * gitlab头部描述
     */
    @NotEmpty(message = "gitlabHead不能为空", groups = {buildPublishGroup.class})
    private String gitlabHead;

    /**
     * gitlab-body描述
     */
    @NotEmpty(message = "gitlabDesc不能为空", groups = {buildPublishGroup.class, deployPublishGroup.class})
    private String gitlabDesc;

    /**
     * 版本号对应作者
     */
    @NotEmpty(message = "pushAuthor不能为空", groups = {buildPublishGroup.class, deployPublishGroup.class})
    private String pushAuthor;

    /**
     * 发布时间
     */
    private Long gitlabTime;

    /**
     * 发布类型
     */
    @NotEmpty(message = "publishType不能为空", groups = {deployPublishGroup.class})
    private String publishType;

    /**
     * 备注
     */
    private String remark;
    /**
     * 备注
     */
    private String beforeRemark;


    private Boolean isJob;


    private String role;
    /**
     * 发布服务器id
     */
    private List<Long> listServerId;

    private String groupName;
    private List<String> groupNameList;

    //网关类型
    @NotBlank(message = "策略不能为空", groups = {updatePublishGroup.class})
    private String gatewayType;


    public interface savePublishGroup {

    }

    public interface updatePublishGroup {
    }

    public interface buildPublishGroup {
    }

    public interface deployPublishGroup {
    }

}
