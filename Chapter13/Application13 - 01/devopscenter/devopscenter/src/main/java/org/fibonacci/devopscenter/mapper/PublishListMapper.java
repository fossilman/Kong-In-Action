package org.fibonacci.devopscenter.mapper;

import org.fibonacci.devopscenter.domain.PublishList;
import model.bo.PublishBo;

import java.util.List;

public interface PublishListMapper{
    int deleteByPrimaryKey(Long id);

    int insert(PublishList record);

    int insertSelective(PublishList record);

    PublishList selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PublishList record);

    int updateByPrimaryKey(PublishList record);

    List<PublishList> listByPublish(PublishBo publishBo);
}
