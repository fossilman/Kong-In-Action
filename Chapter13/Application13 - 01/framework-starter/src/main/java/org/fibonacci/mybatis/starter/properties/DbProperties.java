package org.fibonacci.mybatis.starter.properties;

import org.fibonacci.mybatis.starter.config.DatabaseConfig;
import org.fibonacci.mybatis.starter.config.RoutingConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

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
@ConfigurationProperties(prefix = DbProperties.PROPERTY_PREFIX)
public class DbProperties {

    public final static String PROPERTY_PREFIX = "fibonacci.db";

    private DatabaseConfig database;
    private RoutingConfig.RoutingDataSourceConfig routing;
    private List<DatabaseConfig> databases = new ArrayList();

    public DatabaseConfig getDatabase() {
        return database;
    }

    public void setDatabase(DatabaseConfig database) {
        this.database = database;
    }

    public List<DatabaseConfig> getDatabases() {
        return databases;
    }

    public void setDatabases(List<DatabaseConfig> databases) {
        this.databases = databases;
    }

    public RoutingConfig.RoutingDataSourceConfig getRouting() {
        return routing;
    }

    public void setRouting(RoutingConfig.RoutingDataSourceConfig routing) {
        this.routing = routing;
    }
}
