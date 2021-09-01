package org.fibonacci.mybatis.starter.config;

import lombok.Builder;
import lombok.Data;

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
public class TransactionConfig {

    private Boolean enable;
    private String beanName;

    public TransactionConfig() {

    }

    public TransactionConfig(boolean enable) {
        this.enable = true;
    }

    public TransactionConfig(boolean enable, String beanName) {
        this.enable = true;
        this.beanName = beanName;
    }

    public Boolean isEnable() {
        return enable;
    }
}
