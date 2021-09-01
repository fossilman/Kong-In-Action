package org.fibonacci.routeplus.mapper;


import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.fibonacci.routeplus.domain.ApplicationServices;

import java.util.List;

public interface ApplicationServicesMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ApplicationServices record);

    int insertSelective(ApplicationServices record);

    ApplicationServices selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ApplicationServices record);

    int updateByPrimaryKey(ApplicationServices record);

    List<ApplicationServices> selectByApplicationId(Integer applicationId);

    ApplicationServices selectByApplicationIdAndPath(Integer applicationId,String path);


    @Select({
            "select * from application_services where kong_services_name = #{serviceName} limit 1"
    })
    @ResultMap("BaseResultMap")
    ApplicationServices getOneByKongServiceName(String serviceName);
}

