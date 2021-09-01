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
@ConfigurationProperties(prefix = HttpClientProperties.PROPERTY_PREFIX)
@Data
public class HttpClientProperties {

    public final static String PROPERTY_PREFIX = "fibonacci.http.client";

    private int maxTotal = 200;
    private int defaultMaxPerRoute = 200;
    private int connectionTimeout = 5000;
    private int socketTimeout = 10000;
}
