package org.fibonacci.devopscenter.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author krame
 * @description：
 * @date ：Created in 2019-09-18 15:28
 */
@Component
public class ShellConstants {

    @Value("${shell.file.ip}")
    public String shellFileIp;

    @Value("${publish.url}")
    public String publishUrl;

    @Value("${check.url}")
    public String checkUrl;

    @Value("${stop.url}")
    public String stopUrl;

}
