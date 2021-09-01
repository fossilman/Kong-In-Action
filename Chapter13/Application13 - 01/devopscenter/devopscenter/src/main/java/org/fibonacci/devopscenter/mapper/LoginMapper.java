package org.fibonacci.devopscenter.mapper;


import org.fibonacci.devopscenter.domain.Login;

public interface LoginMapper{

    int deleteByPrimaryKey(Long id);

    int insert(Login record);

    int insertSelective(Login record);

    Login selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Login record);

    int updateByPrimaryKey(Login record);

    Login queryLogin(String name, String password);
}
