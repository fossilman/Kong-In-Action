package org.fibonacci.devopscenter.job;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.RandomUtils;
import org.fibonacci.devopscenter.mapper.ListDeployMapper;
import org.fibonacci.devopscenter.service.PublishService;
import org.fibonacci.framework.exceptions.HttpClientException;
import org.fibonacci.framework.exceptions.ServerException;
import lombok.extern.slf4j.Slf4j;
import model.PublishVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * @author krame
 * @description：检测发布结果
 * @date ：Created in 2019-09-16 13:46
 */
@Slf4j
@Component
public class CheckPublishJob implements InitializingBean {

    /**
     * 发布服务
     */
    @Resource
    private PublishService publishService;

    @Resource
    private ListDeployMapper listDeployMapper;


    public void execute() {
        //1.查询待发布的应用
        log.info("定时检查发布开始执行");
        long start = System.currentTimeMillis();
        List<Long> idList = listDeployMapper.selectByPublishStatus();
        Map<Long, String> result = new HashMap<>();

        //2.检查编译结果
        if (!CollectionUtils.isEmpty(idList)) {
            for (Long id : idList) {
                try {
                    PublishVo publishVo = publishService.checkDeploy(id, true);
                    result.put(id, publishVo.getDeployStatus());
                } catch (ServerException be) {
                    log.error("检查发布业务异常", be);
                } catch (Exception e) {
                    log.error("检查发布系统异常", e);
                }

            }
        }

        log.info("定时检查发布执行结果:" + JSON.toJSONString(result));
        log.info("定时检查发布结果执行完毕" + (System.currentTimeMillis() - start) + "/ms");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        new CheckPublishThread().start();
    }

    private class CheckPublishThread extends Thread {
        private boolean shutdown = false;

        @Override
        public void run() {
            while (shutdown) {
                synchronized (this) {
                    try {
                        wait(10000L + 30 * RandomUtils.nextInt(0, 100));
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

















