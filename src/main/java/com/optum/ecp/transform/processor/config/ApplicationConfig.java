package com.optum.ecp.transform.processor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "app.config")
@Validated
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApplicationConfig {

    private String inputTopic;

    private Map<String, Map<String, String>> queries;

    public String getInputTopic() {
        return inputTopic;
    }

    public void setInputTopic(String inputTopic) {
        this.inputTopic = inputTopic;
    }

    public Map<String, Map<String, String>> getQueries() {
        return queries;
    }

    public void setQueries(Map<String, Map<String, String>> queries) {
        this.queries = queries;
    }
}
