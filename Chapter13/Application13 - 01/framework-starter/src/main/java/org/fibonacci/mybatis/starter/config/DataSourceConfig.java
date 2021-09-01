package org.fibonacci.mybatis.starter.config;

import org.fibonacci.mybatis.starter.config.vo.DataSourceModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Connection;

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
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceConfig {

    private String beanName;
    private String url;
    private Boolean defaultAutoCommit;
    private Boolean defaultReadOnly;
    private int defaultTransactionIsolation = Connection.TRANSACTION_READ_COMMITTED;
    private String driverClassName = "com.mysql.jdbc.Driver";
    private String username;
    private String password;
    private String connectionProperties;
    private int maxActive = 20;
    private Integer maxIdle;
    private int initialSize = 10;
    private Integer minIdle;
    private int maxWait = 60000;
    private boolean testOnBorrow = false;
    private boolean testOnConnect = false;
    private boolean testOnReturn = false;
    private boolean testWhileIdle = true;
    private int timeBetweenEvictionRunsMillis = 60000;
    private int minEvictableIdleTimeMillis = 300000;
    private boolean removeAbandoned = false;
    private int removeAbandonedTimeout = 600;
    private String connectionInitSqls;
    private DataSourceModel model = DataSourceModel.DEFAULT;
    private VaultConfig vault;
}
