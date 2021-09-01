package model.bo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author krame
 * @description：发布对象
 * @date ：Created in 2019-09-17 16:26
 */
@Data
public class LoginBo extends BaseBo {

    @NotBlank(message = "名称不能为空")
    private String name;

    @NotBlank(message = "密码不能为空")
    private String password;

    private Integer id;

    private String username;

    private String status;

    private Integer roleId;

    private String role;

    private List<Group> groups;

    private List<Integer> dataId;

    @Data
    public static class Group{

        private Integer id;

        private String groupName;

        private String defaultGroups;

    }


}
