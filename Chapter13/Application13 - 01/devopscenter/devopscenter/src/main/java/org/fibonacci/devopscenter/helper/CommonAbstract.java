package org.fibonacci.devopscenter.helper;

import org.fibonacci.devopscenter.domain.PublishList;
import model.bo.PublishBo;

/**
 * @author krame
 * @description： 构建公共抽象
 * @date ：Created in 2019-09-17 14:51
 */
public abstract class CommonAbstract {

    /**
     * 初始化检查
     * @param publishBo
     * @return
     */
    public abstract PublishList initCheckDeploy(PublishBo publishBo);


}
