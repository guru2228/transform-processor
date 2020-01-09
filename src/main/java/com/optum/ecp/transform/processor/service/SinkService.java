package com.optum.ecp.transform.processor.service;

import com.optum.ecp.transform.processor.config.ApplicationConfig;
import com.optum.ecp.transform.processor.repo.NewSQLJdbcRepository;
import com.optum.ecp.transform.processor.repo.OracleJdbcRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
public class SinkService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SinkService.class);

    NewSQLJdbcRepository newSQLJdbcRepository;

    OracleJdbcRepository oracleJdbcRepository;

    public SinkService(NewSQLJdbcRepository newSQLJdbcRepository, OracleJdbcRepository oracleJdbcRepository){
        this.newSQLJdbcRepository = newSQLJdbcRepository;
        this.oracleJdbcRepository = oracleJdbcRepository;
    }

    public int[] selectAndInsert(String table, Map<String, String> queries, List<String> primaryKeys) {
        try {
            long startTime = System.currentTimeMillis();
            List<Map<String, Object>> params = oracleJdbcRepository.findByKeys(queries.get("select-query"), primaryKeys);
            LOGGER.info("Time spent in {} table in Oracle SELECT {} ms", table, (System.currentTimeMillis() - startTime));
            startTime = System.currentTimeMillis();
//            for(Map<String, Object> param: params) {
//                int rows = newSQLJdbcRepository.executeSingleInsert(queries.get("insert-query"), param);
//            }
            int rows[] = newSQLJdbcRepository.executeBatchOps(queries.get("insert-query"), params);
            LOGGER.info("Spent time in {} table in NewSQL INSERT {} ms", table, (System.currentTimeMillis() - startTime));
            return new int[]{0};
        }catch (Exception e){
            LOGGER.error("Execption occurred in selectAndInsert", e);
            return new int[]{0};
        }
    }
}
