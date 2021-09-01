package org.fibonacci.mybatis.starter.autoconfigure;

import com.alibaba.druid.filter.Filter;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.github.pagehelper.PageInterceptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.fibonacci.framework.util.ConfigUtil;
import org.fibonacci.mybatis.starter.config.DataSourceConfig;
import org.fibonacci.mybatis.starter.config.DatabaseConfig;
import org.fibonacci.mybatis.starter.config.LogConfig;
import org.fibonacci.mybatis.starter.config.MyBatisConfig;
import org.fibonacci.mybatis.starter.config.RoutingConfig;
import org.fibonacci.mybatis.starter.config.TransactionConfig;
import org.fibonacci.mybatis.starter.config.vo.CustomDruidDataSource;
import org.fibonacci.mybatis.starter.config.vo.DataSourceModel;
import org.fibonacci.mybatis.starter.log.DruidLogFilter;
import org.fibonacci.mybatis.starter.log.LogInfoInterceptor;
import org.fibonacci.mybatis.starter.log.TomcatJdbcLogFilter;
import org.fibonacci.mybatis.starter.properties.DbProperties;
import org.fibonacci.mybatis.starter.routing.RoutingDataSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

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
@EnableConfigurationProperties({DbProperties.class})
@EnableTransactionManagement(proxyTargetClass = true)
@Slf4j
public class DatabaseAutoConfiguration
        implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private Environment environment;
    public static String contextPath;
    public static String serverPort;
    public static String serverHost;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        contextPath = ConfigUtil.getConfig(environment,"server.servlet.context-path",String.class)==null?"":ConfigUtil.getConfig(environment,"server.servlet.context-path",String.class);
        serverPort = ConfigUtil.getConfig(environment,"server.port",String.class)==null?"8080":ConfigUtil.getConfig(environment,"server.port",String.class);
        serverHost = ConfigUtil.getConfig(environment,"server.host",String.class)==null?"http://127.0.0.1":"http://"+ConfigUtil.getConfig(environment,"server.host",String.class);

        DbProperties dbProperties = ConfigUtil.getConfig(environment, DbProperties.PROPERTY_PREFIX,
                DbProperties.class);
        // 用户没有配置db
        Assert.notNull(dbProperties, DbProperties.PROPERTY_PREFIX + " 配置未找到!");
        if (dbProperties.getDatabase() == null && dbProperties.getDatabases() != null && dbProperties
                .getDatabases().size() == 1) {
            dbProperties.setDatabase(dbProperties.getDatabases().get(0));
        }
        initAllDataAccessBean(dbProperties, registry);
    }

    protected void initAllDataAccessBean(DbProperties dbProperties, BeanDefinitionRegistry registry) {
        // 单数据源
        if (dbProperties.getDatabase() != null) {
            initDataAccessBean(dbProperties.getDatabase(), null, registry);
        }
        // 多数据源
        else if (dbProperties.getDatabases() != null && dbProperties.getDatabases().size() > 0) {
            for (int i = 0; i < dbProperties.getDatabases().size(); i++) {
                initDataAccessBean(dbProperties.getDatabases().get(i), i, registry);
            }
        }

        // 注册动态数据源
        if (dbProperties.getRouting() != null) {
            // 初始化多数据源
            registry.registerBeanDefinition(dbProperties.getRouting()
                    .getBeanName(), getRoutingDataSourceBeanDefinition(dbProperties));

            // 注册事务
            TransactionConfig transactionConfig = dbProperties.getRouting().getTransaction();
            // 用户未显示定义，默认为true
            if (transactionConfig == null || transactionConfig.isEnable()) {
                if (transactionConfig == null) {
                    transactionConfig = new TransactionConfig();
                    transactionConfig.setEnable(true);
                    dbProperties.getRouting().setTransaction(transactionConfig);
                }

                if (StringUtils.isEmpty(transactionConfig.getBeanName())) {
                    String beanName = "transactionManager";
                    transactionConfig.setBeanName(beanName);
                }

                registry.registerBeanDefinition(transactionConfig
                        .getBeanName(), getTransactionManagerBeanDefinition(dbProperties.getRouting().getBeanName()));
            }
        }
    }


    /**
     * 注册一个datasource的所有bean信息
     *
     * @param databaseConfig
     * @param index
     * @param registry
     */
    protected void initDataAccessBean(DatabaseConfig databaseConfig, Integer index, BeanDefinitionRegistry registry) {
        DataSourceConfig dataSourceConfig = databaseConfig.getDataSource();
        TransactionConfig transactionConfig = databaseConfig.getTransaction();
        MyBatisConfig myBatisConfig = databaseConfig.getMybatis();
        LogConfig logConfig = databaseConfig.getLog();
        RoutingConfig.RoutingParameterConfig routingConfig = databaseConfig.getRouting();

        // 给与默认 dataSource bean id: dataSource${index}
        if (StringUtils.isEmpty(dataSourceConfig.getBeanName())) {
            if (index == null) {
                dataSourceConfig.setBeanName("dataSource");
            } else {
                dataSourceConfig.setBeanName("dataSource" + index);
            }
        }

        // 注册datasource
        AbstractBeanDefinition dataSourceBeanDefinition = getDruidDataSourceBeanDefinition(dataSourceConfig, logConfig);
        registry.registerBeanDefinition(dataSourceConfig.getBeanName(), dataSourceBeanDefinition);

        // 注册事务
        if ((transactionConfig != null && transactionConfig.isEnable()) || // 用户显示定义事务
                // 用户未显示定义事务，同时也没有routing datasource， 默认值为true
                ((routingConfig == null || StringUtils
                        .isEmpty(routingConfig.getRoutingDataSourceBeanName())) && transactionConfig == null)) {
            if (transactionConfig == null) {
                transactionConfig = new TransactionConfig();
                transactionConfig.setEnable(true);
                databaseConfig.setTransaction(transactionConfig);
            }

            if (StringUtils.isEmpty(transactionConfig.getBeanName())) {
                String beanName = null;
                if (index == null) {
                    beanName = "transactionManager";
                } else {
                    beanName = "transactionManager" + index;
                }
                transactionConfig.setBeanName(beanName);
            }

            registry.registerBeanDefinition(transactionConfig
                    .getBeanName(), getTransactionManagerBeanDefinition(dataSourceConfig.getBeanName()));
//            if (index == null) {
//            } else {
//                registry.registerBeanDefinition(transactionConfig
//                        .getBeanName(), getTransactionManagerBeanDefinition(dataSourceConfig.getBeanName()));
//            }
        }

        // 注册mybatis
        if (myBatisConfig != null) {
            String sqlSessionFactoryBeanName = "sqlSessionFactory";
            String mapperScannerConfigurerBeanName = "mapperScannerConfigurer";
            if (index != null) {
                sqlSessionFactoryBeanName = sqlSessionFactoryBeanName + index;
                mapperScannerConfigurerBeanName = mapperScannerConfigurerBeanName + index;
            }

            registry.registerBeanDefinition(sqlSessionFactoryBeanName, getSqlSessionFactoryBeanDefinition(myBatisConfig, dataSourceConfig, logConfig));
            registry.registerBeanDefinition(mapperScannerConfigurerBeanName, getMapperScannerConfigurerBeanDefinition(myBatisConfig, sqlSessionFactoryBeanName));

            // 给与默认 dataSource bean id: sqlSession${index}
            if (StringUtils.isEmpty(myBatisConfig.getSqlSessionBeanName())) {
                if (index == null) {
                    myBatisConfig.setSqlSessionBeanName("sqlSession");
                } else {
                    myBatisConfig.setSqlSessionBeanName("sqlSession" + index);
                }
            }
            registry.registerBeanDefinition(myBatisConfig
                    .getSqlSessionBeanName(), getSqlSessionBeanDefinition(sqlSessionFactoryBeanName));
        }
    }


    /**
     * 创建动态数据源的bean定义
     *
     * @param dbProperties
     * @return
     */
    protected AbstractBeanDefinition getRoutingDataSourceBeanDefinition(DbProperties dbProperties) {
        BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder
                .rootBeanDefinition(RoutingDataSource.class);
        // 设置策略
        definitionBuilder.addConstructorArgReference(dbProperties.getRouting().getPolicyBeanName());

        // 注入数据源
        ManagedMap<String, Object> map = new ManagedMap();
        for (int i = 0; i < dbProperties.getDatabases().size(); i++) {
            DatabaseConfig databaseConfig = dbProperties.getDatabases().get(i);
            RoutingConfig.RoutingParameterConfig routing = databaseConfig.getRouting();

            String originalBeanName = databaseConfig.getDataSource().getBeanName();
            Assert.hasText(routing.getRoutingDataSourceBeanName(), "routing.routingDataSourceBeanName is blank!");
            if (routing != null
                    // 目前只有一个routing数据源，当有多个数据源，根据bean name一一对应
                    && dbProperties.getRouting().getBeanName().equals(routing.getRoutingDataSourceBeanName())) {
                Assert.hasText(routing.getKey(), "routing.key is blank!");
                map.put(routing.getKey(), new RuntimeBeanReference(originalBeanName));
            }
            if (routing.isDefault()) {
                definitionBuilder
                        .addPropertyReference("defaultTargetDataSource", originalBeanName);
            }
        }
        definitionBuilder.addPropertyValue("targetDataSources", map);
        definitionBuilder.addPropertyValue("lenientFallback", dbProperties.getRouting().isLenientFallback());

        AbstractBeanDefinition bd = definitionBuilder.getRawBeanDefinition();
        bd.setScope(BeanDefinition.SCOPE_SINGLETON);

        return bd;
    }

    /**
     * 获取druid 数据源
     *
     * @param dataSourceConfig
     * @param logConfig
     * @return
     */
    private static AbstractBeanDefinition getDruidDataSourceBeanDefinition(DataSourceConfig dataSourceConfig, LogConfig logConfig) {
        if (dataSourceConfig.getMinIdle() == null) {
            dataSourceConfig.setMinIdle(dataSourceConfig.getInitialSize());
        }
        if (dataSourceConfig.getMaxIdle() == null) {
            dataSourceConfig.setMaxIdle(dataSourceConfig.getMaxActive());
        }

//        BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder
//                .rootBeanDefinition(com.alibaba.druid.pool.DruidDataSource.class);
        BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(CustomDruidDataSource.class);
        definitionBuilder.setInitMethodName("init");
        definitionBuilder.setDestroyMethodName("close");

        definitionBuilder.addPropertyValue("model", dataSourceConfig.getModel());
        definitionBuilder.addPropertyValue("vault", dataSourceConfig.getVault());
        if(dataSourceConfig.getModel() == DataSourceModel.VAULT) {
            // TODO 暂时屏蔽Vault
//            definitionBuilder.addPropertyReference("vaultPropertySourceMap", VaultPros.CUSTOM_LEASE_VAULT_PS_BEAN_NAME);
        }

        // add filter
        if (logConfig.isSlowSqlOn()) {
            DruidLogFilter filter = new DruidLogFilter();
            filter.setSlowLogOn(logConfig.isSlowSqlOn());
            filter.setSlowSqlThreshold(logConfig.getSlowSqlThreshold());
            List<Filter> filters = new ArrayList();
            filters.add(filter);
            definitionBuilder.addPropertyValue("proxyFilters", filters);
        }
        // if not set, use default value in jdbc driver
        if (dataSourceConfig.getDefaultAutoCommit() != null) {
            definitionBuilder
                    .addPropertyValue("defaultAutoCommit", dataSourceConfig.getDefaultAutoCommit());
        }
        if (dataSourceConfig.getDefaultReadOnly() != null) {
            definitionBuilder
                    .addPropertyValue("defaultReadOnly", dataSourceConfig.getDefaultReadOnly());
        }
        definitionBuilder
                .addPropertyValue("defaultTransactionIsolation", dataSourceConfig.getDefaultTransactionIsolation());

        definitionBuilder.addPropertyValue("url", dataSourceConfig.getUrl());
        definitionBuilder.addPropertyValue("username", dataSourceConfig.getUsername());
        definitionBuilder.addPropertyValue("password", dataSourceConfig.getPassword());
        definitionBuilder.addPropertyValue("driverClassName", dataSourceConfig.getDriverClassName());

        // 配置初始化大小、最小、最大
        definitionBuilder.addPropertyValue("initialSize", dataSourceConfig.getInitialSize());
        definitionBuilder.addPropertyValue("minIdle", dataSourceConfig.getMinIdle());
        definitionBuilder.addPropertyValue("maxActive", dataSourceConfig.getMaxActive());

        // 配置获取连接等待超时的时间
        definitionBuilder.addPropertyValue("maxWait", dataSourceConfig.getMaxWait());
        // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫
        definitionBuilder
                .addPropertyValue("timeBetweenEvictionRunsMillis", dataSourceConfig.getTimeBetweenEvictionRunsMillis());
        // 配置一个连接在池中最小生存的时间，单位是毫秒
        definitionBuilder
                .addPropertyValue("minEvictableIdleTimeMillis", dataSourceConfig.getMinEvictableIdleTimeMillis());

        definitionBuilder.addPropertyValue("validationQuery", "select 1");
        definitionBuilder.addPropertyValue("testWhileIdle", dataSourceConfig.isTestWhileIdle());
        definitionBuilder.addPropertyValue("testOnBorrow", dataSourceConfig.isTestOnBorrow());
        definitionBuilder.addPropertyValue("testOnReturn", dataSourceConfig.isTestOnReturn());

        definitionBuilder.addPropertyValue("removeAbandoned", dataSourceConfig.isRemoveAbandoned());
        definitionBuilder.addPropertyValue("removeAbandonedTimeout", dataSourceConfig.getRemoveAbandonedTimeout());

        definitionBuilder.addPropertyValue("keepAlive", "true");
        // 打开PSCache，并且指定每个连接上PSCache的大小
        definitionBuilder.addPropertyValue("poolPreparedStatements", "true");
        definitionBuilder.addPropertyValue("maxPoolPreparedStatementPerConnectionSize", "20");
        definitionBuilder.addPropertyValue("connectionInitSqls", dataSourceConfig.getConnectionInitSqls());


        AbstractBeanDefinition bd = definitionBuilder.getRawBeanDefinition();
        bd.setScope(BeanDefinition.SCOPE_SINGLETON);

        return bd;
    }

    /**
     * 获取 tomcat jdbc datasource bean 定义
     *
     * @param dataSourceConfig
     * @return
     */
    protected AbstractBeanDefinition getTomcatJdbcDataSourceBeanDefinition(DataSourceConfig dataSourceConfig, LogConfig logConfig) {
        if (dataSourceConfig.getMinIdle() == null) {
            dataSourceConfig.setMinIdle(dataSourceConfig.getInitialSize());
        }
        if (dataSourceConfig.getMaxIdle() == null) {
            dataSourceConfig.setMaxIdle(dataSourceConfig.getMaxActive());
        }

        BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder
                .rootBeanDefinition(org.apache.tomcat.jdbc.pool.DataSource.class);
        definitionBuilder.setDestroyMethodName("close");

        // add filter
        definitionBuilder
                .addPropertyValue("jdbcInterceptors", "ConnectionState;StatementFinalizer;" +
                        (logConfig.isSlowSqlOn() ? (TomcatJdbcLogFilter.class
                                .getName() + "(slowLogOn=" + logConfig
                                .isSlowSqlOn() + ",slowSqlThreshold=" + logConfig.getSlowSqlThreshold() + ")") : ""));

        // if not set, use default value in jdbc driver
        if (dataSourceConfig.getDefaultAutoCommit() != null) {
            definitionBuilder
                    .addPropertyValue("defaultAutoCommit", dataSourceConfig.getDefaultAutoCommit());
        }
        if (dataSourceConfig.getDefaultReadOnly() != null) {
            definitionBuilder
                    .addPropertyValue("defaultReadOnly", dataSourceConfig.getDefaultReadOnly());
        }
        definitionBuilder
                .addPropertyValue("defaultTransactionIsolation", dataSourceConfig.getDefaultTransactionIsolation());

        definitionBuilder.addPropertyValue("url", dataSourceConfig.getUrl());
        definitionBuilder.addPropertyValue("username", dataSourceConfig.getUsername());
        definitionBuilder.addPropertyValue("password", dataSourceConfig.getPassword());
        definitionBuilder.addPropertyValue("driverClassName", dataSourceConfig.getDriverClassName());

        // 配置初始化大小、最小、最大
        definitionBuilder.addPropertyValue("initialSize", dataSourceConfig.getInitialSize());
        definitionBuilder.addPropertyValue("minIdle", dataSourceConfig.getMinIdle());
        definitionBuilder.addPropertyValue("maxActive", dataSourceConfig.getMaxActive());
        definitionBuilder.addPropertyValue("maxIdle", dataSourceConfig.getMaxIdle());

        // 配置获取连接等待超时的时间
        definitionBuilder.addPropertyValue("maxWait", dataSourceConfig.getMaxWait());
        // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫
        definitionBuilder
                .addPropertyValue("timeBetweenEvictionRunsMillis", dataSourceConfig.getTimeBetweenEvictionRunsMillis());
        // 配置一个连接在池中最小生存的时间，单位是毫秒
        definitionBuilder
                .addPropertyValue("minEvictableIdleTimeMillis", dataSourceConfig.getMinEvictableIdleTimeMillis());

        definitionBuilder.addPropertyValue("validationQuery", "select 1");
        definitionBuilder.addPropertyValue("testOnConnect", dataSourceConfig.isTestOnConnect());
        definitionBuilder.addPropertyValue("testWhileIdle", dataSourceConfig.isTestWhileIdle());
        definitionBuilder.addPropertyValue("testOnBorrow", dataSourceConfig.isTestOnBorrow());
        definitionBuilder.addPropertyValue("testOnReturn", dataSourceConfig.isTestOnReturn());

        definitionBuilder.addPropertyValue("removeAbandoned", dataSourceConfig.isRemoveAbandoned());
        definitionBuilder.addPropertyValue("removeAbandonedTimeout", dataSourceConfig.getRemoveAbandonedTimeout());
        definitionBuilder.addPropertyValue("connectionInitSqls", dataSourceConfig.getConnectionInitSqls());

        AbstractBeanDefinition bd = definitionBuilder.getRawBeanDefinition();
        bd.setScope(BeanDefinition.SCOPE_SINGLETON);

        return bd;
    }

    /**
     * 获取mybaits SqlSessionFactory bean 定义
     *
     * @param dataSourceConfig
     * @return
     */
    protected AbstractBeanDefinition getSqlSessionFactoryBeanDefinition(MyBatisConfig myBatisConfig, DataSourceConfig dataSourceConfig, LogConfig logConfig) {
        BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder
                .rootBeanDefinition(com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean.class);
        definitionBuilder.addPropertyReference("dataSource", dataSourceConfig.getBeanName());

        PaginationInterceptor pageInterceptor = new PaginationInterceptor();
        if (myBatisConfig.getPage().isSupportMethodsArguments()) {
            Properties pageInterceptorProperties = new Properties();
            pageInterceptorProperties.setProperty("supportMethodsArguments", "true");
            pageInterceptorProperties.setProperty("params", "pageNum=pageNum;pageSize=pageSize");
            pageInterceptor.setProperties(pageInterceptorProperties);
        }

        // LogInfoInterceptor 放在最后，以确保这个Interceptor在pageInterceptor之前被调用. refer https://github.com/pagehelper/Mybatis-PageHelper/blob/master/wikis/zh/Interceptor.md
        if (logConfig.isSlowSqlOn()) {
            definitionBuilder
                    .addPropertyValue("plugins", new Interceptor[]{pageInterceptor, new LogInfoInterceptor()});
        }

        AbstractBeanDefinition bd = definitionBuilder.getRawBeanDefinition();
        bd.setScope(BeanDefinition.SCOPE_SINGLETON);

        return bd;
    }

    /**
     * 获取mybaits MapperScannerConfigurer bean 定义
     *
     * @param myBatisConfig
     * @param sqlSessionFactoryBeanName
     * @return
     */
    protected AbstractBeanDefinition getMapperScannerConfigurerBeanDefinition(MyBatisConfig myBatisConfig, String sqlSessionFactoryBeanName) {
        BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder
                .rootBeanDefinition(org.mybatis.spring.mapper.MapperScannerConfigurer.class);
        definitionBuilder
                .addPropertyValue("sqlSessionFactoryBeanName", sqlSessionFactoryBeanName);
        definitionBuilder.addPropertyValue("basePackage", myBatisConfig.getBasePackage());

        AbstractBeanDefinition bd = definitionBuilder.getRawBeanDefinition();
        bd.setScope(BeanDefinition.SCOPE_SINGLETON);

        return bd;
    }

    /**
     * 获取mybaits SqlSessionTemplate bean 定义
     *
     * @param sqlSessionFactoryBeanName
     * @return
     */
    protected AbstractBeanDefinition getSqlSessionBeanDefinition(String sqlSessionFactoryBeanName) {
        BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder
                .rootBeanDefinition(org.mybatis.spring.SqlSessionTemplate.class);
        definitionBuilder.addConstructorArgReference(sqlSessionFactoryBeanName);
        AbstractBeanDefinition bd = definitionBuilder.getRawBeanDefinition();
        bd.setScope(BeanDefinition.SCOPE_SINGLETON);

        return bd;
    }

    /**
     * 获取 DataSourceTransactionManager bean定义
     *
     * @param dataSourceBeanName
     * @return
     */
    protected AbstractBeanDefinition getTransactionManagerBeanDefinition(String dataSourceBeanName) {
        BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder
                .genericBeanDefinition(DataSourceTransactionManager.class);
        definitionBuilder.addConstructorArgReference(dataSourceBeanName);
        AbstractBeanDefinition bd = definitionBuilder.getRawBeanDefinition();
        bd.setScope(BeanDefinition.SCOPE_SINGLETON);

        return bd;
    }
}
