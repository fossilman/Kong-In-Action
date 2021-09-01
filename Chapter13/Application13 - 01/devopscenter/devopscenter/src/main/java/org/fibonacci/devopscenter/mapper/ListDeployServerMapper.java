package org.fibonacci.devopscenter.mapper;

import org.fibonacci.devopscenter.domain.ListDeployServer;
import model.bo.ShellBo;

import java.util.List;

public interface ListDeployServerMapper{
    int deleteByPrimaryKey(Long id);

    int insert(ListDeployServer record);

    int insertSelective(ListDeployServer record);

    ListDeployServer selectByPrimaryKey(Long id);

    List<ListDeployServer> selectByVersion(Long deployid, String version);

    List<ListDeployServer> selectByDeployid(Long deployid);

    List<ListDeployServer> selectBylistid(Long listid);

    int updateByPrimaryKeySelective(ListDeployServer record);

    int updateByPrimaryKey(ListDeployServer record);

    int updateByIps(ShellBo shellBoListhell);

    int deleteByDeployId(List<Long> deployIds);
}
