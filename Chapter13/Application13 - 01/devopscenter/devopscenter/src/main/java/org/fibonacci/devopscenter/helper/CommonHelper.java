package org.fibonacci.devopscenter.helper;

import org.fibonacci.framework.httpclient.HttpClientTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author krame
 * @description： common
 * @date ：Created in 2019-09-17 14:51
 */
@Component
@Slf4j
public class CommonHelper {

    @Resource
    protected HttpClientTemplate httpClientTemplate;


}
