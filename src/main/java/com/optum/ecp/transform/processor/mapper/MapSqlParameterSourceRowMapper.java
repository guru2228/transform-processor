package com.optum.ecp.transform.processor.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.optum.ecp.transform.processor.service.PayloadProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.JdbcUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MapSqlParameterSourceRowMapper implements RowMapper<Map<String, Object>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapSqlParameterSourceRowMapper.class);

    public MapSqlParameterSourceRowMapper(){
    }

    public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        for (int index = 1; index <= columnCount; index++) {
            String column = JdbcUtils.lookupColumnName(rsmd, index).toLowerCase();
            Object value = rs.getObject(column);
            if (value == null) {
                parameters.putIfAbsent(column, value);
            } else if (value instanceof Integer) {
                parameters.putIfAbsent(column, (Integer) value);
            } else if (value instanceof String) {
                parameters.putIfAbsent(column, (String) value);
            } else if (value instanceof Boolean) {
                parameters.putIfAbsent(column, (Boolean) value);
            } else if (value instanceof Date) {
                parameters.putIfAbsent(column, rs.getDate(index));
            }else if (value instanceof Timestamp || value instanceof oracle.sql.TIMESTAMP) {
                //String timeStamp = rs.getTimestamp(index).toString();
                parameters.putIfAbsent(column, rs.getTimestamp(index));
            }else if (value instanceof Long) {
                parameters.putIfAbsent(column, (Long) value);
            } else if (value instanceof Double) {
                parameters.putIfAbsent(column, (Double) value);
            } else if (value instanceof Float) {
                parameters.putIfAbsent(column, (Float) value);
            } else if (value instanceof BigDecimal) {
                parameters.putIfAbsent(column, (BigDecimal) value);
            } else if (value instanceof Byte) {
                parameters.putIfAbsent(column, (Byte) value);
            } else if (value instanceof byte[]) {
                parameters.putIfAbsent(column, (byte[]) value);
            } else if (value instanceof BigInteger) {
                parameters.putIfAbsent(column, (Integer) value);
            }else {
                throw new IllegalArgumentException("Unmappable object type: " + value.getClass());
            }
        }
        //LOGGER.debug("Parameters : {}", parameters.getValues());
        return parameters;
    }
}