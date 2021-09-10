package org.fibonacci.devopscenter.job;

import org.apache.commons.lang3.RandomUtils;
import org.fibonacci.devopscenter.mapper.ListBuildMapper;
import org.fibonacci.devopscenter.service.PublishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.fibonacci.framework.exceptions.HttpClientException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/*
 * @author krame
 * @description：检查jenkins发布状态
 * @date ：Created in 2019-09-16 13:46
 */
@Slf4j
@Component
public class CheckBuildJob implements InitializingBean {

    /**
     * 发布服务
     */
    @Resource
    private PublishService publishService;

    @Resource
    private ListBuildMapper listBuildMapper;

    public void execute() {
        log.info("定时检查编译开始执行");
        //1.查询待发布的应用
        long start = System.currentTimeMillis();
        List<Long> listBuilds = listBuildMapper.selectByBuildStatus();

        //2.检查编译结果
        if (!CollectionUtils.isEmpty(listBuilds)) {
            for (Long id : listBuilds) {
                try {
                    publishService.checkBuild(id);
                } catch (Exception e) {
                    log.error("检查编译系统异常", e);
                }

            }
        }
        log.info("定时检查编译结果执行完毕,耗时:" + (System.currentTimeMillis() - start) + "/ms");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        new CheckBuildThread().start();
    }

    private class CheckBuildThread extends Thread {
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
