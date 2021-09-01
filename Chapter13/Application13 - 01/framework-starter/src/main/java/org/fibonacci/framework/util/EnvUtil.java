package org.fibonacci.framework.util;

import org.fibonacci.framework.global.Env;
import org.fibonacci.framework.global.EnvironmentVariable;
import org.apache.commons.lang3.StringUtils;

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
public class EnvUtil {

    private static final String DEFAULT_CLUSTER_NAME = "local";
    private static String clusterName;
    private static Env env;

    static {
        String clusterNameFromEnv = getEnvironmentVariable(EnvironmentVariable.CLUSTER_NAME);
        if (!StringUtils.isEmpty(clusterNameFromEnv)) {
            clusterName = clusterNameFromEnv;
        } else {
            clusterName = DEFAULT_CLUSTER_NAME;
        }
        if (DEFAULT_CLUSTER_NAME.equalsIgnoreCase(clusterName)) {
            env = Env.local;
        } else {
            String[] splitedClusterName = clusterName.split("_");
            env = Env.valueOf(splitedClusterName[1]);
        }
    }

    public static String getClusterName() {
        return clusterName;
    }
    public static Env getEnv() {
        return env;
    }
    public static String getEnvironmentVariable(EnvironmentVariable ev) {
        return System.getenv(ev.name());
    }
    public static boolean isLocal(){
        return env.equals(Env.local);
    }
    public static boolean isDev(){
        return env.equals(Env.dev);
    }
    public static boolean isSit(){
        return env.equals(Env.sit);
    }
    public static boolean isProd(){
        return env.equals(Env.prod);
    }
}
