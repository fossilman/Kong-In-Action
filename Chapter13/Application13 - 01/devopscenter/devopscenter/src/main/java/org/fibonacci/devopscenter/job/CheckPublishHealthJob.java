package org.fibonacci.devopscenter.job;

import lombok.extern.slf4j.Slf4j;
import model.bo.PublishBo;
import org.apache.commons.lang3.RandomUtils;
import org.fibonacci.devopscenter.domain.ListDeploy;
import org.fibonacci.devopscenter.helper.DeployAbstract;
import org.fibonacci.devopscenter.mapper.ListDeployMapper;
import org.fibonacci.framework.exceptions.HttpClientException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
public class CheckPublishHealthJob implements InitializingBean {

    @Resource
    private ListDeployMapper listDeployMapper;

    @Resource
    private DeployAbstract deployAbstract;

    public void execute() {

        List<Long> deployIds = listDeployMapper.allIngDeployIds();
        for (Long deployId : deployIds) {
            try {
                PublishBo publishBo = new PublishBo();
                publishBo.setDeployId(deployId);
                publishBo.setIsJob(false);
                ListDeploy listDeploy = listDeployMapper.selectById(publishBo.getDeployId());
                String result = deployAbstract.checkDeploy(publishBo);
                log.info("第 {} 次检查应用 {} 发布状态 {}", listDeploy.getLooping(), listDeploy.getListName(), result);
                listDeployMapper.incrLoopingById(deployId);

            } catch (Exception e) {
                log.error("检查发布镜像系统异常", e);
            }
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        new CheckPublishHealthThread().start();
    }

    private class CheckPublishHealthThread extends Thread {
        private boolean shutdown = false;

        @Override
        public void run() {
            while (shutdown) {
                synchronized (this) {
                    try {
                        wait(1000L + 30 * RandomUtils.nextInt(0, 100));
                        execute();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (HttpClientException ex) {
                        log.error("something wrong when calling feign client, message:{}", ex.getMessage(), ex);
                    } catch (Exception e) {
                        log.error("something wrong.", e);
                    }
                }

            }
        }
    }
}
