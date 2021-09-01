package org.fibonacci.routeplus.model.bo;

import org.fibonacci.routeplus.model.RouteVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author ：zachary
 * @description：路由对象
 * @date ：Created in 2019-09-17 10:57
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteBo extends BaseBo {


    //应用id
    @NotNull(message = "应用id不能为空", groups = {queryRoute.class,saveRoute.class})
    private Integer applicationId;
    @NotNull(message = "应用name不能为空", groups = {saveRoute.class})
    private String applicationName;

    private Integer id;
    @NotBlank(message = "名称不能为空", groups = {saveRoute.class})
    private String name;
    @NotBlank(message = "类型不能为空", groups = {saveRoute.class})
    private String type;
    @Valid
    @NotNull(message = "plugins不能为空", groups = {saveRoute.class})
    private List<RouteVo.Plugins> plugins;
    private Boolean enabled = true;

    private RouteInnerService services;
    private InnerRoute routes;
    private String routeType;

    @Data
    public static class RouteInnerService {
        private Integer id;
        private Boolean enabled;
    }

    @Data
    public static class InnerRoute {
        private Integer id;
        private Boolean enabled;
    }

    public interface queryRoute {
    }

    public interface saveRoute {
    }

    public interface enabledRoute {
    }

    public interface deleteRoute {
    }

    public interface checkEnabled {
    }

}
