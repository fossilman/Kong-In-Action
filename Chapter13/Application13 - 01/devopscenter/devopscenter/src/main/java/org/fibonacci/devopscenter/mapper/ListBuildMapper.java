package org.fibonacci.devopscenter.mapper;

import org.fibonacci.devopscenter.domain.ListBuild;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ListBuildMapper{
    int deleteByPrimaryKey(Long id);

    int deleteByListid(Long listid);

    int insert(ListBuild record);

    int insertSelective(ListBuild record);

    ListBuild selectByPrimaryKey(Long id);

    List<ListBuild> selectBylistid(Long listid);

    int updateByPrimaryKeySelective(ListBuild record);

    int updateByPrimaryKey(ListBuild record);

    ListBuild selectByListidAndVersion(Long listid, String gitlabVersion);

    List<ListBuild> selectByListid(Long listid);

    List<ListBuild> selectBylistIdArrays(@Param("listidList") List<Long> listidList);

    List<ListBuild> selectByAll();

    List<Long> selectByBuildStatus();

    ListBuild selectByHarborKey(String harborkey);

    List<ListBuild> selectByHarborArray(List<String> harborkey);
}
