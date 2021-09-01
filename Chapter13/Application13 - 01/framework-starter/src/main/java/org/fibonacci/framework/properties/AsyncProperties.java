package org.fibonacci.framework.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
@ConfigurationProperties(prefix = AsyncProperties.PROPERTY_PREFIX)
@Data
public class AsyncProperties {

    public final static String PROPERTY_PREFIX = "fibonacci.async.executor";

    private int corePoolSize = 5;
    private int maxPoolSize = 10;
    private int keepAliveSeconds = 60;
    private int queueCapacity = 20;
}
