package org.fibonacci.mybatis.starter.log;

import com.alibaba.fastjson.JSON;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jdbc.pool.JdbcInterceptor;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.tomcat.jdbc.pool.interceptor.AbstractCreateStatementInterceptor;
import org.fibonacci.framework.threadlocal.ParameterThreadLocal;

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
@Slf4j
public class TomcatJdbcLogFilter extends AbstractCreateStatementInterceptor {

    private static final String DEFAULT_REQUEST_METHOD = "EXECUTE";
    private boolean slowLogOn = false;
    private int slowSqlThreshold = 1000;

    @Override
    public void closeInvoked() {
    }

    @Override
    public void setProperties(Map<String, PoolProperties.InterceptorProperty> properties) {
        super.setProperties(properties);
        final String slowSqlThreshold = "slowSqlThreshold";
        final String slowLogOn = "slowLogOn";
        PoolProperties.InterceptorProperty p1 = properties.get(slowSqlThreshold);
        PoolProperties.InterceptorProperty p2 = properties.get(slowLogOn);
        if (p1 != null) {
            this.slowSqlThreshold = Integer.parseInt(p1.getValue());
        }
        if (p2 != null) {
            this.slowLogOn = Boolean.parseBoolean(p2.getValue());
        }
    }

    protected void reportSlowQuery(String sql, long start, long delta) {
        if (sql == null || sql.length() == 0) {
            return;
        }
        String requestMethod = null;
        int i = sql.indexOf(' ');
        if (i > 0) {
            requestMethod = sql.substring(0, i).toUpperCase();
        } else {
            requestMethod = DEFAULT_REQUEST_METHOD;
        }
        Map<String, Object> logMap = new LinkedHashMap<>();
        logMap.put("requestId", ParameterThreadLocal.getRequestId());
        Map<String, Object> map = LogInfo.info.get();
        String requestApi = null;
        if (map != null) {
            requestApi = (String) LogInfo.info.get().get(LogInfo.KEY_API);
        } else {
            requestApi = DEFAULT_REQUEST_METHOD;
        }
        logMap.put("requestApi", requestApi);
        logMap.put("requestMethod", requestMethod);
        logMap.put("requestTime", start);
        logMap.put("responseInterval", delta);
        logMap.put("slowSql", sql.trim());
        log.info("[MysqlSlow]: " + JSON.toJSONString(logMap));
    }

    /**
     * Creates a statement interceptor to monitor query response times
     */
    @Override
    public Object createStatement(Object proxy, Method method, Object[] args, Object statement, long time) {
        try {
            Object result = null;
            String name = method.getName();
            String sql = null;
            Constructor<?> constructor = null;
            if (compare(CREATE_STATEMENT, name)) {
                //createStatement
                constructor = getConstructor(CREATE_STATEMENT_IDX, Statement.class);
            } else if (compare(PREPARE_STATEMENT, name)) {
                //prepareStatement
                sql = (String) args[0];
                constructor = getConstructor(PREPARE_STATEMENT_IDX, PreparedStatement.class);
            } else if (compare(PREPARE_CALL, name)) {
                //prepareCall
                sql = (String) args[0];
                constructor = getConstructor(PREPARE_CALL_IDX, CallableStatement.class);
            } else {
                //do nothing, might be a future unsupported method
                //so we better bail out and let the system continue
                return statement;
            }
            result = constructor.newInstance(new StatementProxy(statement, sql));
            return result;
        } catch (Exception x) {
            log.warn("Unable to create statement proxy for slow query report.", x);
        }
        return statement;
    }

    /**
     * Class to measure query execute time
     */
    protected class StatementProxy implements InvocationHandler {
        protected boolean closed = false;
        protected Object delegate;
        protected final String query;

        public StatementProxy(Object parent, String query) {
            this.delegate = parent;
            this.query = query;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //get the name of the method for comparison
            final String name = method.getName();
            //was close invoked?
            boolean close = compare(JdbcInterceptor.CLOSE_VAL, name);
            //allow close to be called multiple times
            if (close && closed) return null;
            //are we calling isClosed?
            if (compare(JdbcInterceptor.ISCLOSED_VAL, name)) return Boolean.valueOf(closed);
            //if we are calling anything else, bail out
            if (closed) throw new SQLException("Statement closed.");
            boolean process = false;
            //check to see if we are about to execute a query
            process = isExecute(method, process);
            //if we are executing, get the current time
            long start = System.currentTimeMillis();
            Object result = null;
            try {
                //execute the query
                result = method.invoke(delegate, args);
            } catch (Throwable t) {
                // reportFailedQuery(getActualSql(), args, name, start, t);
                if (t instanceof InvocationTargetException
                        && t.getCause() != null) {
                    throw t.getCause();
                } else {
                    throw t;
                }
            }
            //see if we meet the requirements to measure
            if (process && slowLogOn) {
                //measure the time
                long delta = System.currentTimeMillis() - start;
                if (delta >= slowSqlThreshold) {
                    try {
                        //report the slow query
                        reportSlowQuery(getActualSql(args, name), start, delta);
                    } catch (Exception t) {
                        if (log.isWarnEnabled()) log.warn("Unable to process slow query", t);
                    }
                }
            } else if (process) {
                // reportQuery(getActualSql(), args, name, start, delta);
            }
            //perform close cleanup
            if (close) {
                closed = true;
                delegate = null;
            }
            return result;
        }

        protected String getActualSql(Object[] args, String methodName) throws SQLException {
            if (delegate instanceof com.mysql.jdbc.PreparedStatement) {
                return ((com.mysql.jdbc.PreparedStatement) delegate).asSql();
            } else {
                String sql = (query == null && args != null && args.length > 0) ? (String) args[0] : query;
                //if we do batch execution, then we name the query 'batch'
                if (sql == null && compare(EXECUTE_BATCH, methodName)) {
                    sql = "batch";
                }
                return sql;
            }
        }
    }

}
