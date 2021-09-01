import base from "./base";
import { ROUTE_PLUS } from "@/config/env";
import { AxiosPromise, AxiosRequestConfig } from "axios";
import authors from "@/config/author/index";

// 路由应用层父列表
export function routesList(applicationId: string, current: number = 1, size: number = 10): AxiosPromise {
  const config: AxiosRequestConfig = {
    url: ROUTE_PLUS + "/routes/list",
    method: "post",
    data: {
      applicationId,
      current,
      size
    }
  };
  return base(config, authors.serverList.router.id);
}

// 路由父应用层添加/修改
export function saveOrUpdate(data: PluginsIn): AxiosPromise {
  const config: AxiosRequestConfig = {
    url: ROUTE_PLUS + "/routes/saveOrUpdate",
    method: "post",
    data
  };
  return base(config, authors.serverList.router.id);
}

// 插件列表
export function routesPlugin(current: number = 1, size: number = 10): AxiosPromise {
  const config: AxiosRequestConfig = {
    url: ROUTE_PLUS + "/routes/plugin",
    method: "get",
    params: {
      current,
      size
    }
  };
  return base(config, authors.serverList.router.id);
}

// 父路由禁用
export function routesEnabled(services?: RouteplusId, routes?: RouteplusId): AxiosPromise {
  const config: AxiosRequestConfig = {
    url: ROUTE_PLUS + "/routes/enabled",
    method: "post",
    data: {
      services,
      routes
    }
  };
  return base(config, authors.serverList.router.id);
}

// 父路由删除
export function routesDelete(data: RouteDel): AxiosPromise {
  const config: AxiosRequestConfig = {
    url: ROUTE_PLUS + "/routes/delete",
    method: "post",
    data
  };
  return base(config, authors.routingcenter.plugDel.id);
}

// 路由应用层子列表
export function servicesList(applicationId: string, current: number = 1, size: number = 10): AxiosPromise {
  const config: AxiosRequestConfig = {
    url: ROUTE_PLUS + "/services/list",
    method: "post",
    data: {
      applicationId,
      current,
      size
    }
  };
  return base(config, authors.serverList.router.id);
}

// 路由网络层子列表
export function servicesRoutesList(serviceId: number, current: number = 1, size: number = 10): AxiosPromise {
  const config: AxiosRequestConfig = {
    url: ROUTE_PLUS + "/services/routes/list",
    method: "post",
    data: {
      serviceId,
      current,
      size
    }
  };
  return base(config, authors.serverList.router.id);
}

// 路由子应用层添加/修改
export function servicesSaveOrUpdate(data: PluginsIn): AxiosPromise {
  const config: AxiosRequestConfig = {
    url: ROUTE_PLUS + "/services/saveOrUpdate",
    method: "post",
    data
  };
  return base(config, authors.routingcenter.add.id);
}

// 路由子网关层添加/修改
export function servicesRoutesSave(data: servicesRoutes): AxiosPromise {
  const config: AxiosRequestConfig = {
    url: ROUTE_PLUS + "/services/routes/saveOrUpdate",
    method: "post",
    data
  };
  return base(config, authors.routingcenter.add.id);
}

// 子路由禁用
export function servicesEnabled(data: ServicesEnabled): AxiosPromise {
  const config: AxiosRequestConfig = {
    url: ROUTE_PLUS + "/services/enabled",
    method: "post",
    data
  };
  return base(config, authors.routingcenter.plugAdd.id);
}

export function checkEnabled(id: number): AxiosPromise {
  const config: AxiosRequestConfig = {
    url: ROUTE_PLUS + "/services/routes/checkEnabled",
    method: "get",
    params: { id }
  };
  return base(config, authors.routingcenter.plugAdd.id);
}

// 子路由删除
export function servicesDel(data: ServicesEnabled): AxiosPromise {
  const config: AxiosRequestConfig = {
    url: ROUTE_PLUS + "/services/delete",
    method: "post",
    data
  };
  return base(config, authors.routingcenter.plugDel.id);
}

// 子路由插件删除
export function pluginDelete(name: string, id?: number): AxiosPromise {
  const config: AxiosRequestConfig = {
    url: ROUTE_PLUS + "/services/plugin/delete",
    method: "get",
    params: {
      id,
      name
    }
  };
  return base(config, authors.routingcenter.plugDel.id);
}

// 给应用添加插件
export function addPlugForApp(data: any): AxiosPromise {
  const config: AxiosRequestConfig = {
    url: ROUTE_PLUS + "/plugins/service",
    method: "post",
    data
  };
  return base(config);
}

// 给路由添加插件
export function addPlugForRoute(data: any): AxiosPromise {
  const config: AxiosRequestConfig = {
    url: ROUTE_PLUS + "/plugins/route",
    method: "post",
    data
  };
  return base(config);
}

// 删除应用插件
export function delPlugForApp(id: number): AxiosPromise {
  const config: AxiosRequestConfig = {
    url: ROUTE_PLUS + "/plugins/service/" + id,
    method: "delete"
  };
  return base(config);
}

// 删除路由插件
export function delPlugForRoute(id: number): AxiosPromise {
  const config: AxiosRequestConfig = {
    url: ROUTE_PLUS + "/plugins/route/" + id,
    method: "delete"
  };
  return base(config);
}
