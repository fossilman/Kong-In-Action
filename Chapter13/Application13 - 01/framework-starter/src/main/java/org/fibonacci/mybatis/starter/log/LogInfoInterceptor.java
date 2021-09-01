package org.fibonacci.mybatis.starter.log;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
})
public class LogInfoInterceptor implements Interceptor {

    private static final String REQUEST_API_PREFIX = "org.fibonacci.";
    private static final String REQUEST_API_POSTFIX = "(...)";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Map<String, Object> map = new HashMap();
        map.put(LogInfo.KEY_API, getRequestApi(mappedStatement.getId()));
        LogInfo.info.set(map);
        try {
            Object returnValue = invocation.proceed();
            return returnValue;
        } finally {
            LogInfo.info.remove();
        }
    }

    private static String getRequestApi(String sqlId) {
        String requestId = sqlId;
        if (sqlId.contains(REQUEST_API_PREFIX)) {
            requestId = sqlId.replace(REQUEST_API_PREFIX, "");
        }
        return requestId.concat(REQUEST_API_POSTFIX);
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
