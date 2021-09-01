package model.bo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author krame
 * @description：服务器对象
 * @date ：Created in 2019-09-17 16:26
 */
@Data
public class ServerBo extends BaseBo {


    @NotNull(message = "id不能为空", groups = {updateServerGroup.class})
    private Long id;

    @NotBlank(message = "ip不能为空", groups = {saveServerGroup.class})
    private String ip;

    @NotBlank(message = "port不能为空", groups = {saveServerGroup.class})
    private String port;

    @NotBlank(message = "名称不能为空", groups = {saveServerGroup.class})
    private String name;

    private String gitlabVersion;

    @NotBlank(message = "组不能为空", groups = {saveServerGroup.class})
    private String team;

    private Integer vagrancy;


    /**
     * 描述
     */
    @NotBlank(message = "描述不能为空", groups = {saveServerGroup.class})
    private String remark;


    public interface saveServerGroup {

    }

    public interface updateServerGroup {
    }

}
