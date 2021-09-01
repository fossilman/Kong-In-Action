package org.fibonacci.devopscenter.mapper;

import org.fibonacci.devopscenter.domain.Server;
import model.bo.ServerBo;

import java.util.List;

public interface ServerMapper{
    int deleteByPrimaryKey(Long id);

    int insert(Server record);

    int insertSelective(Server record);

    Server selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Server record);

    int updateByPrimaryKey(Server record);

    List<Server> listByServer(ServerBo serverBo);

    List<Server> listByTeam(String team);
}
