package com.zanyizan.piggy.smt.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

@Data
@org.springframework.context.annotation.Configuration
@MapperScan(
        annotationClass = Mapper.class,
        basePackages = "com.zanyizan.piggy.smt.dao",
        sqlSessionFactoryRef = "sqlSessionFactory"
)
public class MysqlConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MysqlConfiguration.class);

    @Value("${spring.datasource.host}")
    private String host;

    @Value("${spring.datasource.port}")
    private int port;

    @Value("${spring.datasource.schema}")
    private String database;

    @Value("${spring.datasource.username}")
    private String userName;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.max-active}")
    private int maxActive;

    @Value("${spring.datasource.max-wait}")
    private int maxWait;

    @Value("${spring.datasource.min-idle}")
    private int minIdle;

    @Value("${spring.datasource.initial-size}")
    private int initialSize;

    @Value("${spring.datasource.timeBetweenEvictionRunsMillis:150000}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${spring.datasource.minEvictableIdleTimeMillis:270000}")
    private int minEvictableIdleTimeMillis;

    @Value("${spring.datasource.testWhileIdle:true}")
    private Boolean testWhileIdle;

    public DataSource dataSource() throws SQLException {
        return dataSource(getHost(), getPort(), getDatabase());
    }

    protected DataSource dataSource(String host, int port, String database) throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://" + host + ":" + port + "/" + database
                + "?useUnicode=true&characterEncoding=utf-8");
        dataSource.setUsername(getUserName());
        dataSource.setPassword(getPassword());
        dataSource.setMaxActive(getMaxActive());
        dataSource.setMinIdle(getMinIdle());
        dataSource.setMaxWait(getMaxWait());
        dataSource.setTestWhileIdle(getTestWhileIdle());
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setTimeBetweenEvictionRunsMillis(getTimeBetweenEvictionRunsMillis());
        dataSource.setMinEvictableIdleTimeMillis(getMinEvictableIdleTimeMillis());
        dataSource.setInitialSize(getInitialSize());
        dataSource.setValidationQuery("SELECT 'x'");
        try {
            dataSource.init();
        } catch (SQLException e) {
            logger.error("init dataSource(" + dataSource.getUrl() + ") fail", e);
            throw e;
        }
        return dataSource;
    }

    @Bean("sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource());
        factory.setVfs(SpringBootVFS.class);
        Configuration config = new Configuration();
        config.setMapUnderscoreToCamelCase(true);
        factory.setConfiguration(config);

        return factory.getObject();
    }

    @Primary
    @Bean("dataSourceTransactionManager")
    public DataSourceTransactionManager transactionManager() throws SQLException{
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource());
        return transactionManager;
    }

}
