package com.optum.ecp.transform.processor.repo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optum.ecp.transform.processor.config.ApplicationConfig;
import com.optum.ecp.transform.processor.mapper.JsonNodeRowMapper;
import com.optum.ecp.transform.processor.mapper.MapSqlParameterSourceRowMapper;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Repository
public class OracleJdbcRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(OracleJdbcRepository.class);

    private ApplicationConfig applicationConfig;

    private HikariDataSource oracleDataSource;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public OracleJdbcRepository(ApplicationConfig applicationConfig, @Qualifier("oracleDataSource") HikariDataSource oracleDataSource) {
        this.applicationConfig = applicationConfig;
        this.oracleDataSource = oracleDataSource;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(oracleDataSource);
    }

    public List<Map<String, Object>> findByKeys(String selectSQL, String primaryKey) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", primaryKey);
        return namedParameterJdbcTemplate.query(selectSQL, parameters, new MapSqlParameterSourceRowMapper());
    }


    public List<Map<String, Object>> findByKeys(String selectSQL, List<String> primaryKeys) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", primaryKeys);
        return namedParameterJdbcTemplate.query(selectSQL, parameters, new MapSqlParameterSourceRowMapper());
    }
}
