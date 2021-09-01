package org.fibonacci.mybatis.starter.routing;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

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
public class RoutingDataSource extends AbstractRoutingDataSource {

    protected RoutingDataSourcePolicy policy = null;

    public RoutingDataSource() {
    }

    public RoutingDataSource(RoutingDataSourcePolicy policy) {
        this.policy = policy;
    }

    public RoutingDataSourcePolicy getPolicy() {
        return policy;
    }

    public void setPolicy(RoutingDataSourcePolicy policy) {
        this.policy = policy;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        Object key = policy.determineCurrentLookupKey();
        return key;
    }
}
