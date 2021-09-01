package org.fibonacci.routeplus.mapper;


import org.fibonacci.routeplus.domain.ServicesRoutePlugin;

import java.util.List;

public interface ServicesRoutePluginMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ServicesRoutePlugin record);

    int insertSelective(ServicesRoutePlugin record);

    ServicesRoutePlugin selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ServicesRoutePlugin record);

    int updateByPrimaryKey(ServicesRoutePlugin record);

    List<ServicesRoutePlugin> selectByServiceIdArrays(List<Integer> ids);
    List<ServicesRoutePlugin> selectByRoutesIdArrays(List<Integer> ids);

    ServicesRoutePlugin selectByServiceIdAndPlugin(Integer serviceId,String pluginName);

    int deleteByServiceId(Integer serviceId);
    int deleteByRouteId(Integer routeId);

    ServicesRoutePlugin selectById(Integer id);

    int batchdeleteByRoutesIds(List<Integer> ids);

}
