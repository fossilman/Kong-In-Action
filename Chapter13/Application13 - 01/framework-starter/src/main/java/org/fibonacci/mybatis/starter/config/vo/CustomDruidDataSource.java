package org.fibonacci.mybatis.starter.config.vo;

import com.alibaba.druid.pool.DruidDataSource;
import org.fibonacci.mybatis.starter.config.VaultConfig;
import lombok.Setter;

import java.sql.SQLException;

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
@Setter
public class CustomDruidDataSource extends DruidDataSource {

    private static final long serialVersionUID = 1L;
    private DataSourceModel model;
    private VaultConfig vault;
//    private LeaseAwareVaultPropertySourceMap vaultPropertySourceMap;

    @Override
    public void init() throws SQLException {
        // TODO 暂时屏蔽Vault的内容
//        Objects.requireNonNull(model);
//        if(model == DataSourceModel.VAULT) {
//            Objects.requireNonNull(vault);
//            Objects.requireNonNull(vaultPropertySourceMap);
//            Objects.requireNonNull(vault.getProName());
//            if(vaultPropertySourceMap.containsKey(vault.getProName())) {
//                LeaseAwareVaultPropertySource vaultPropertySource = vaultPropertySourceMap.get(vault.getProName());
//                String username = null;
//                String password = null;
//                for(String item : vaultPropertySource.getPropertyNames()) {
//                    if(item.endsWith("username")) {
//                        username = vaultPropertySource.getProperty(item).toString();
//                    }
//                    if(item.endsWith("password")) {
//                        password = vaultPropertySource.getProperty(item).toString();
//                    }
//                }
//                this.setUsername(username);
//                this.setPassword(password);
//            }
//        }
        super.init();
    }
}
