package org.fibonacci.routeplus.mapper;


import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.fibonacci.routeplus.domain.ServicesRoutes;

import java.util.List;

public interface ServicesRoutesMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ServicesRoutes record);

    int insertSelective(ServicesRoutes record);

    ServicesRoutes selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ServicesRoutes record);

    int updateByPrimaryKey(ServicesRoutes record);

    List<ServicesRoutes> selectByServiceIdArrays(List<Integer> ids);

    int deleteByServiceId(Integer serviceId);

    ServicesRoutes selectByServiceIdAndType(Integer serviceId, String type);

    int batchdeleteByIds(List<Integer> ids);

    ServicesRoutes selectByServiceIdAndPath(Integer serviceId, String outPath);

    @Select({
            "select * from services_routes where kong_routes_name = #{routeName} limit 1"
    })
    @ResultMap("BaseResultMap")
    ServicesRoutes getOneByKongRouteName(String routeName);

}
