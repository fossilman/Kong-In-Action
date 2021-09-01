package org.fibonacci.routeplus.model.bo;

import org.fibonacci.routeplus.domain.ApplicationServices;
import org.fibonacci.routeplus.domain.ServicesRoutes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author ：zachary
 * @description：路由对象
 * @date ：Created in 2019-09-17 10:57
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceBo extends BaseBo {


    //应用id
    @NotNull(message = "应用id不能为空", groups = {queryService.class})
    private Integer applicationId;
    //应用名称
    private String applicationName;

    //服务列表
    @NotNull(groups = {saveService.class})
    @Valid
    private ApplicationServices services;

    //服务列表
    @NotNull(groups = {saveServiceRoute.class})
    @Valid
    private ServicesRoutes routesPool;
    @NotNull(groups = {saveServiceRoute.class,queryServiceRoutes.class})
    private Integer serviceId;


    public interface queryService {
    }

    public interface saveService {
    }

    public interface saveServiceRoute {
    }

    public interface enabledService {
    }

    public interface deleteService {
    }

    public interface queryServiceRoutes {
    }

}
