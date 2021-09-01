package org.fibonacci.framework.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * <p>
 * Copyright (C) 2020 Shanghai LuoJin Com., Ltd. All rights reserved.
 * <p>
 * No parts of this file may be reproduced or transmitted in any form or by any means,
 * electronic, mechanical, photocopying, recording, or otherwise, without prior written
 * permission of Shanghai LuoJin Com., Ltd.
 *
 * @author krame
 * @date 2020/11/25
 */
@Configuration
@ConfigurationProperties(prefix = "fibonacci.build")
@PropertySource(value = "classpath:/META-INF/build-info.properties", ignoreResourceNotFound = true)
public class BuildInfo {

    private String time;
    private String artifact;
    private String group;
    private Integer version = 0;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getArtifact() {
        return artifact;
    }

    public void setArtifact(String artifact) {
        this.artifact = artifact;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Integer getVersion() {
        return version;
    }

    // TODO 完善 version 解析策略
    public void setVersion(String version) {
//        if (!StringUtils.isEmpty(version)) {
//            int i = version.lastIndexOf('-');
//            // dev：0.0.1-20190111.110536-1
//            // sit：1.0.0-107
//            // prod: 1.0.0.24
//            if (i == -1) {
//                i = version.lastIndexOf('.');
//            }
//            if (i != -1) {
//                version = version.substring(i + 1);
//            }
//            try {
//                this.version = Integer.valueOf(version);
//            } catch (Exception e) {
//            }
//        }
    }
}
