package org.fibonacci.devopscenter.helper;

import model.bo.PublishBo;

/**
 * @author krame
 * @description： 构建抽象
 * @date ：Created in 2019-09-17 14:51
 */
public abstract class DeployAbstract extends CommonAbstract{

    /**
     * 执行发布
     * @param publishBo
     * @return
     */
    public abstract Long executeDeploy(PublishBo publishBo);

    /**
     * 预发布
     * @param publishBo
     * @return
     */
    public abstract String executeDeployLog(PublishBo publishBo);

    /**
     * 检查发布
     * @param publishBo
     * @return
     */
    public abstract String checkDeploy(PublishBo publishBo);

}
