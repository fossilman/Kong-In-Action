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
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyBatisConfig {

    private String basePackage = "org.fibonacci.**.mapper";
    private String sqlSessionBeanName;
    private PageConfig page = new PageConfig();

    public static class PageConfig {

        private boolean supportMethodsArguments = true;
        public boolean isSupportMethodsArguments() {
            return supportMethodsArguments;
        }
        public void setSupportMethodsArguments(boolean supportMethodsArguments) {
            this.supportMethodsArguments = supportMethodsArguments;
        }
    }
}
