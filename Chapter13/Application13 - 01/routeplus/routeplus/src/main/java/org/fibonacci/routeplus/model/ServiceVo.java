package org.fibonacci.routeplus.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.fibonacci.routeplus.domain.ApplicationServices;
import org.fibonacci.routeplus.domain.ServicesRoutes;
import org.fibonacci.routeplus.utils.DateToLong;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
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
public class ServiceVo {


    //服务列表
    private List<ApplicationServices> services = new ArrayList<>();
    private List<ServicesRoutes> routes = new ArrayList<>();

    private Integer serviceId;
    private Integer routeId;

    private Boolean hasEnabled;

    private String path;
    private String outPath;
    private String remark;
    private String updateBy;
    @JsonSerialize(using = DateToLong.class)
    private Date updateTime;
    private String gatewayTypes;
    private Integer applicationId;
    private String applicationName;
    private List<RouteVo.Plugins> plugins;
}
