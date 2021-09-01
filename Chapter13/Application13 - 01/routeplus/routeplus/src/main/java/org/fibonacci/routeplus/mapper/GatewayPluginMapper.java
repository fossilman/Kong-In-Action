package org.fibonacci.routeplus.mapper;


import org.fibonacci.routeplus.domain.GatewayPlugin;

import java.util.List;

public interface GatewayPluginMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GatewayPlugin record);

    int insertSelective(GatewayPlugin record);

    GatewayPlugin selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GatewayPlugin record);

    int updateByPrimaryKey(GatewayPlugin record);

    List<GatewayPlugin> listGatewayPlugin(Integer applicationId);

    GatewayPlugin selectByApplicationId(Integer applicationId,String type,Integer pluginId);
}
