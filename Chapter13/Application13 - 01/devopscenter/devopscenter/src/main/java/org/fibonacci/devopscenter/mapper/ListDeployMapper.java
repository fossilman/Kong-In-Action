package org.fibonacci.devopscenter.mapper;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.fibonacci.devopscenter.domain.ListDeploy;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ListDeployMapper{
    int deleteByPrimaryKey(Long id);

    int insert(ListDeploy record);

    int insertSelective(ListDeploy record);

    ListDeploy selectByPrimaryKey(Long id);

    ListDeploy selectById(Long id);

    List<ListDeploy> selectBylistid(Long listid, Long id);

    ListDeploy selectBylistidforLaster(Long listid);

    List<ListDeploy> selectBylistIdArray(@Param("listidList") List<Long> listidList);

    int updateByPrimaryKeySelective(ListDeploy record);

    int updateByPrimaryKey(ListDeploy record);

    List<ListDeploy> selectByAll();

    List<Long> selectByPublishStatus();

    List<Long> selectByDeployId(Long listid);

    int deleteByListid(Long listid);

    @Update({
            "update pb_list_deploy ",
            "set looping = looping + 1 ",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int incrLoopingById(@Param("id") Long id);

    @Select({"select id from pb_list_deploy where publish_status = 'ING'"})
    List<Long> allIngDeployIds();

    @Select({
            "select * from  ",
            "pb_list_deploy where list_id = #{0} order by create_time desc limit 1"
    })
    @ResultMap("BaseResultMap")
    ListDeploy selectLastOneByListId(Long listId);

    /*int batchInsert(Map map);*/
}
