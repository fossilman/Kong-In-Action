package org.fibonacci.mybatis.starter.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class RoutingConfig {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoutingDataSourceConfig {

        private String beanName;
        private String policyBeanName;
        private boolean lenientFallback = true;
        private TransactionConfig transaction = new TransactionConfig(true);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoutingParameterConfig {

        private String routingDataSourceBeanName;
        private String key;
        private boolean isDefault = false;
    }
}
