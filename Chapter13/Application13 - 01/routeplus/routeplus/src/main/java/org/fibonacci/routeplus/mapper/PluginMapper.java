package org.fibonacci.routeplus.mapper;


import org.fibonacci.routeplus.domain.Plugin;

import java.util.List;

public interface PluginMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Plugin record);

    int insertSelective(Plugin record);

    Plugin selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Plugin record);

    int updateByPrimaryKey(Plugin record);

    List<Plugin> listPlugin();
}
