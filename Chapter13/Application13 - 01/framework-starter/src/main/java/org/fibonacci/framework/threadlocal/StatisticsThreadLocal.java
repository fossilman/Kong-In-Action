package org.fibonacci.framework.threadlocal;

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
public class StatisticsThreadLocal {

    private static ThreadLocal<String> apiName = new ThreadLocal<String>();
    public static String getApiName() {
        return apiName.get();
    }
    public static void setApiName(String value) {
        apiName.set(value);
    }
    public static void clear() {
        apiName.set(null);
    }
}

