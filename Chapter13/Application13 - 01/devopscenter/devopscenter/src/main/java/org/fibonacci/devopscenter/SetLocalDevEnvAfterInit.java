package org.fibonacci.devopscenter;

import org.fibonacci.framework.global.AppInfo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetLocalDevEnvAfterInit implements InitializingBean {

    @Autowired
    private AppInfo appInfo;

    @Override
    public void afterPropertiesSet() throws Exception {
        if ("local".equalsIgnoreCase(appInfo.getEnv())) {
            appInfo.setEnv("dev");
            appInfo.setClusterName("dev");
        }
    }
}
