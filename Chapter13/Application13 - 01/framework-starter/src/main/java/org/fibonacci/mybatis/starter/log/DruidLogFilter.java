package org.fibonacci.mybatis.starter.log;

import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.proxy.jdbc.JdbcParameter;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.fibonacci.framework.httpclient.HttpClientTemplate;
import org.fibonacci.framework.logcontrol.HttpLogItem;
import org.fibonacci.framework.logcontrol.LogControl;
import org.fibonacci.framework.threadlocal.ParameterThreadLocal;
import org.fibonacci.mybatis.starter.autoconfigure.DatabaseAutoConfiguration;
import org.springframework.beans.factory.annotation.Value;

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
public class DruidLogFilter extends Slf4jLogFilter {

    @Value("${spring.application.name:unkown}")
    private String appName;
    @Value("${server.address:localhost}")
    private String serverIp;
    @Value("${server.port:8080}")
    private Integer serverPort;

    private SQLUtils.FormatOption statementSqlFormatOption = new SQLUtils.FormatOption(false, false);
    private static final String DEFAULT_REQUEST_METHOD = "EXECUTE";
    private boolean slowLogOn = false;
    private int slowSqlThreshold = 1000;
    private static final HttpClientTemplate httpClinetTemplate = new HttpClientTemplate(200, 200,
            5000, 10000);

    public boolean isSlowLogOn() {
        return slowLogOn;
    }
    public void setSlowLogOn(boolean slowLogOn) {
        this.slowLogOn = slowLogOn;
    }
    public int getSlowSqlThreshold() {
        return slowSqlThreshold;
    }
    public void setSlowSqlThreshold(int slowSqlThreshold) {
        this.slowSqlThreshold = slowSqlThreshold;
    }

    @Override
    public boolean isConnectionLogErrorEnabled() {
        return false;
    }

    @Override
    public boolean isDataSourceLogEnabled() {
        return false;
    }

    @Override
    public boolean isConnectionLogEnabled() {
        return false;
    }

    @Override
    public boolean isStatementLogEnabled() {
        return false;
    }

    @Override
    public boolean isResultSetLogEnabled() {
        return false;
    }

    @Override
    public boolean isResultSetLogErrorEnabled() {
        return false;
    }

    @Override
    public boolean isStatementLogErrorEnabled() {
        return false;
    }

    private ThreadLocal<Long> startTime = new ThreadLocal();

    @Override
    protected void statementExecuteBefore(StatementProxy statement, String sql) {
        startTime.set(System.currentTimeMillis());
    }

    @Override
    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean firstResult) {
        if (slowLogOn) {
            long delta = System.currentTimeMillis() - startTime.get();
            if (delta >= slowSqlThreshold) {
                reportSlowQuery(getSql(statement, sql), startTime.get(), delta);
            }
        }
    }

    @Override
    protected void statementExecuteBatchBefore(StatementProxy statement) {
        startTime.set(System.currentTimeMillis());
    }

    @Override
    protected void statementExecuteBatchAfter(StatementProxy statement, int[] result) {
        if (slowLogOn) {
            long delta = System.currentTimeMillis() - startTime.get();
            if (delta >= slowSqlThreshold) {
                String sql;
                if (statement instanceof PreparedStatementProxy) {
                    sql = ((PreparedStatementProxy) statement).getSql();
                } else {
                    sql = statement.getBatchSql();
                }
                reportSlowQuery(getSql(statement, sql), startTime.get(), delta);
            }
        }
    }

    @Override
    protected void statementExecuteQueryBefore(StatementProxy statement, String sql) {
        startTime.set(System.currentTimeMillis());
    }

    @Override
    protected void statementExecuteQueryAfter(StatementProxy statement, String sql, ResultSetProxy resultSet) {
        if (slowLogOn) {
            long delta = System.currentTimeMillis() - startTime.get();
            if (delta >= slowSqlThreshold) {
                reportSlowQuery(getSql(statement, sql), startTime.get(), delta);
            }
        }
    }

    @Override
    protected void statementExecuteUpdateBefore(StatementProxy statement, String sql) {
        startTime.set(System.currentTimeMillis());
    }

    @Override
    protected void statementExecuteUpdateAfter(StatementProxy statement, String sql, int updateCount) {
        if (slowLogOn) {
            long delta = System.currentTimeMillis() - startTime.get();
            if (delta >= slowSqlThreshold) {
                reportSlowQuery(getSql(statement, sql), startTime.get(), delta);
            }
        }
    }

    private String getSql(StatementProxy statement, String sql) {
        int parametersSize = statement.getParametersSize();
        if (parametersSize == 0) {
            return sql;
        }

        List<Object> parameters = new ArrayList<Object>(parametersSize);
        for (int i = 0; i < parametersSize; ++i) {
            JdbcParameter jdbcParam = statement.getParameter(i);
            parameters.add(jdbcParam != null
                    ? jdbcParam.getValue()
                    : null);
        }

        String dbType = statement.getConnectionProxy().getDirectDataSource().getDbType();
        String formattedSql = SQLUtils.format(sql, dbType, parameters, statementSqlFormatOption);
        return formattedSql;
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
        Map<HttpLogItem, Object> logMap = new LinkedHashMap<>();
        logMap.put(HttpLogItem.requestId, ParameterThreadLocal.getRequestId());
        Map<String, Object> map = LogInfo.info.get();
        String requestApi = null;
        if (map != null) {
            requestApi = (String) LogInfo.info.get().get(LogInfo.KEY_API);
        } else {
            requestApi = DEFAULT_REQUEST_METHOD;
        }
        logMap.put(HttpLogItem.requestApi, requestApi);
        logMap.put(HttpLogItem.requestMethod, requestMethod);
        logMap.put(HttpLogItem.requestTime, start);
        logMap.put(HttpLogItem.responseInterval, delta);
        logMap.put(HttpLogItem.slowSql, sql.trim());
        logMap.put(HttpLogItem.logType, LogControl.LogType.SlowSql);
        log.info(JSON.toJSONString(logMap));
        httpClinetTemplate.doGet(DatabaseAutoConfiguration.serverHost+":"+DatabaseAutoConfiguration.serverPort+ DatabaseAutoConfiguration.contextPath +"/slowSqlIncrement");
    }
}
