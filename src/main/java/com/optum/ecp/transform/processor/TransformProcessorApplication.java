package com.optum.ecp.transform.processor;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.postgresql.ds.PGConnectionPoolDataSource;
import org.postgresql.ds.PGPoolingDataSource;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableKafka
public class TransformProcessorApplication {

    public static void main (String args[]){
        SpringApplication.run(TransformProcessorApplication.class, args);
    }



    @Bean({"datasource", "newsqlDataSource"})
    @Primary
    @Profile("cockroach-mbr")
    public HikariDataSource newsqlDataSource() {

//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("org.postgresql.Driver");
//        dataSource.setUrl("jdbc:postgresql://apvrp47071:31475,apvrp47072:31475,apvrp47074:31475,apvrp47075:31475,apvrp47076:31475,apvrp47077:31475,apvrp47081:31475,apvrp47082:31475,apvrp47083:31475/clinical?sslmode=disable&loadBalanceHosts=true&useServerPrepStmts=false&rewriteBatchedStatements=true");
//        dataSource.setUsername("root");

        PGSimpleDataSource pds = new PGSimpleDataSource();
//        pds.setServerNames(new String[]{"apvrp47071","apvrp47072","apvrp47074","apvrp47075","apvrp47076","apvrp47077","apvrp47081","apvrp47082","apvrp47083"});
//        pds.setPortNumbers(new int[]{31475,31475,31475,31475,31475,31475,31475,31475,31475});
        pds.setUrl("jdbc:postgresql://apvrp47071:31475,apvrp47072:31475,apvrp47074:31475,apvrp47075:31475,apvrp47076:31475,apvrp47077:31475,apvrp47081:31475,apvrp47082:31475,apvrp47083:31475/clinical?sslmode=disable&loadBalanceHosts=true&useServerPrepStmts=false&rewriteBatchedStatements=true");
//        pds.setURL("jdbc:postgresql://apvrp47071:31475,apvrp47072:31475,apvrp47074:31475,apvrp47075:31475,apvrp47076:31475,apvrp47077:31475,apvrp47081:31475,apvrp47082:31475,apvrp47083:31475/clinical?sslmode=disable&loadBalanceHosts=true&useServerPrepStmts=false&rewriteBatchedStatements=true");
        pds.setDatabaseName("clinical");
        pds.setUser("root");
        pds.setReWriteBatchedInserts(true);

        HikariConfig config = new HikariConfig();
        config.setDataSource(pds);
        config.setMaximumPoolSize(25);
        config.setMinimumIdle(20);
        config.setPoolName("cockroach-spring-boot");
//        config.setDriverClassName("org.postgresql.Driver");
//        config.setJdbcUrl("jdbc:postgresql://apvrp47071:31475,apvrp47072:31475,apvrp47074:31475,apvrp47075:31475,apvrp47076:31475,apvrp47077:31475,apvrp47081:31475,apvrp47082:31475,apvrp47083:31475/clinical?sslmode=disable&loadBalanceHosts=true&useServerPrepStmts=false&rewriteBatchedStatements=true");
//        config.setUsername("root");
        config.setAutoCommit(true);
        Properties props = new Properties();
        props.setProperty("sslmode","disable");
        props.setProperty("loadBalanceHosts","true");
        props.setProperty("useServerPrepStmts","false");
        props.setProperty("rewriteBatchedStatements","true");
        config.setDataSourceProperties(props);
        // We will wait for 15 seconds to get a connection from the pool.
        // Default is 30, but it shouldn't be taking that long.
        config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(60)); // 15000

        // If a connection is not returned within 10 seconds, it's probably safe to assume it's been leaked.
        //config.setLeakDetectionThreshold(TimeUnit.SECONDS.toMillis(30)); // 10000
        return new  HikariDataSource(config);
    }

    @Bean({"datasource", "newsqlDataSource"})
    @Primary
    @Profile("yugabyte-mbr")
    public HikariDataSource dataSource() {
        HikariConfig config = new HikariConfig();

        config.setMaximumPoolSize(25);
        config.setMinimumIdle(20);
        config.setPoolName("yugabyte-spring-boot");
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl("jdbc:postgresql://apvrp47071:32517,apvrp47072:32517,apvrp47074:32517,apvrp47075:32517,apvrp47076:32517,apvrp47077:32517,apvrp47081:32517,apvrp47082:32517,apvrp47083:32517/clinical?sslmode=disable&loadBalanceHosts=true&useServerPrepStmts=false&rewriteBatchedStatements=true");
        config.setUsername("postgres");
        config.setAutoCommit(true);
        // We will wait for 15 seconds to get a connection from the pool.
        // Default is 30, but it shouldn't be taking that long.
        config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(60)); // 15000
//        YBClusterAwareDataSource ds = new YBClusterAwareDataSource();
//        ds.setInitialHost("apvrp47070");
//        ds.setPort(32517);
//        ds.setDatabase("clinical");
//        ds.setUser("postgres");
//        ds.setMaxPoolSizePerNode(12);
//        ds.setAutoCommit(true);
//        ds.setConnectionTimeoutMs(3600000);
        return new HikariDataSource(config);
    }

    @Bean({ "oracleDataSource"})
    public HikariDataSource oracleDataSource() {
        HikariConfig config = new HikariConfig();

        config.setMaximumPoolSize(25);
        config.setMinimumIdle(20);
        config.setPoolName("oracle-spring-boot");
        config.setDriverClassName("oracle.jdbc.OracleDriver");
        config.setJdbcUrl("jdbc:oracle:thin:@orass0032:1521:icust011");
        config.setUsername("uhg_001021759");
        
        config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(60));

        // If a connection is not returned within 10 seconds, it's probably safe to assume it's been leaked.
        //config.setLeakDetectionThreshold(TimeUnit.SECONDS.toMillis(30)); // 10000
        return new  HikariDataSource(config);
    }

    @Bean("new-sql-health")
    @Profile({"cockroach-mbr","yugabyte-mbr"})
    public DataSourceHealthIndicator newsqlDataSourceHealthIndicator(@Qualifier("newsqlDataSource") HikariDataSource newsqlDataSource) {
        return new DataSourceHealthIndicator(newsqlDataSource);
    }

    @Bean("oracle-health")
    public DataSourceHealthIndicator oracleDataSourceHealthIndicator(@Qualifier("oracleDataSource") HikariDataSource oracleDataSource) {
        return new DataSourceHealthIndicator(oracleDataSource);
    }
}
