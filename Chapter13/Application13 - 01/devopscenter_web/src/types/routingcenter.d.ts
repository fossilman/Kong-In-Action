interface PluginAttr {
    quantity: number;
    period: string;
}
interface PluginsIn {
    id?: number;
    applicationId: number;
    applicationName: string;
    name: string;
    type: string;
    plugins: Plugins[];
}

interface GatewayType {
    name: string;
    type: string;
}

interface Plugins {
    id?: number;
    pluginAttr: PluginAttr;
    pluginId?: number;
    pluginName: string;
    enabled: boolean;
}

interface PluginsData {
    applicationId?: number;
    applicationName?: string;
    name?: string;
    type?: string;
    enabled?: boolean;
    Plugins?: Plugins;
}

interface Gateways {
    type: string;
    plugins: [];
}


interface PluginsType {
    id?: number;
    pluginId: number;
    pluginName: string;
    enabled?: boolean;
}

interface RouteplusId {
    id?: number;
}

interface RouteDel {
    services?: RouteplusId;
    routes?: RouteplusId;
}

interface RouteServices {
    serviceId?: number;
    path: string;
    enabled: boolean;
    remark: string;
    updateBy?: string;
    updateTime?: number;
    routesPool: RoutesPool[] | [] | RoutesPoolSet[];
    plugins?: Plugins | [];
}

interface RoutesPool {
    routeId?: number;
    routeType?: string;
    name?: string;
    innerPath?: string;
    outPath?: string;
    hasEnabled?: boolean;
    remark?: string;
    updateTime?: number;
    updateBy?: string;
    plugins?: Plugins | [];
    enabled?: boolean;
}

interface servicesRoutes {
    serviceId?: number;
    routesPool: RoutesPool;
}

interface RoutesPoolSet {
    routeId: number;
    routeType: String;
}

interface RoutesDetail {
    gatewayType: string[];
    applicationName: string;
    applicationId: string;
}

interface serviceIdObj {
    id?: number;
    serviceId?: number;
    enabled?: boolean;
}
interface routeIdObj {
    id?: number;
    routeId?: number;
    enabled?: boolean;
}

interface ServicesEnabled {
    services?: serviceIdObj;
    routes?: routeIdObj;
    routeId?: routeIdObj;
}
