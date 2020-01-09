package com.optum.ecp.transform.processor.repo;

import com.optum.ecp.transform.processor.config.ApplicationConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Repository
public class NewSQLJdbcRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewSQLJdbcRepository.class);

    private ApplicationConfig applicationConfig;

    private HikariDataSource newsqlDataSource;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public NewSQLJdbcRepository(ApplicationConfig applicationConfig, HikariDataSource newsqlDataSource) {
        this.applicationConfig = applicationConfig;
        this.newsqlDataSource = newsqlDataSource;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(newsqlDataSource);
    }

    //@Transactional
    public int executeSingleInsert(String insertSQL,  Map<String, Object> params) {
        SqlParameterSource batchParams = new MapSqlParameterSource(params);
        return namedParameterJdbcTemplate.update(insertSQL, batchParams);
    }

    public int[] executeBatchOps(String insertSQL,  List<Map<String, Object>> params) {
        SqlParameterSource[] batchParams = SqlParameterSourceUtils.createBatch(params);
        return namedParameterJdbcTemplate.batchUpdate(insertSQL, batchParams);
    }



}
