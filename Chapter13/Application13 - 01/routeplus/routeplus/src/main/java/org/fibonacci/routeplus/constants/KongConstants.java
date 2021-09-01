package org.fibonacci.routeplus.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author ：zachary
 * @description：
 * @date ：Created in 2019-09-18 15:28
 */
@Component
public class KongConstants {

    @Value("${kong.url}")
    public String kongUrl;

    @Value("${kong.upstream}")
    public String kongUpstream;

    @Value("${kong.upstream.name}")
    public String kongUpstreamName;

    @Value("${kong.target}")
    public String kongTarget;

    @Value("${kong.target.save}")
    public String kongTargetSave;

    @Value("${kong.target.del}")
    public String kongTargetDel;

    @Value("${kong.services}")
    public String kongServices;

    @Value("${kong.services.del}")
    public String kongServicesDel;

    @Value("${kong.routes}")
    public String kongRoutes;

    @Value("${kong.routes.update}")
    public String kongRouteUpdate;

    @Value("${kong.routeslist}")
    public String kongRouteslist;

    @Value("${kong.routes.detail}")
    public String kongRoutesDetail;


    @Value("${kong.routes.del}")
    public String kongRoutesDel;

    @Value("${kong.status}")
    public String kongStatus;

    @Value("${kong.services.plugin}")
    public String kongServicesPlugin;

    @Value("${kong.route.plugin}")
    public String kongRoutePlugin;

    @Value("${kong.plugin}")
    public String kongPlugin;
}
