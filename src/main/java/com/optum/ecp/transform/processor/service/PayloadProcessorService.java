package com.optum.ecp.transform.processor.service;

import com.optum.ecp.transform.processor.config.ApplicationConfig;
import com.optum.ecp.transform.processor.repo.NewSQLJdbcRepository;
import com.optum.ecp.transform.processor.repo.OracleJdbcRepository;
import org.apache.commons.collections4.ListUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class PayloadProcessorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PayloadProcessorService.class);

    SinkService sinkService;

    ApplicationConfig applicationConfig;

    public PayloadProcessorService(SinkService sinkService, ApplicationConfig applicationConfig){
        this.sinkService = sinkService;
        this.applicationConfig = applicationConfig;
    }

    @KafkaListener(topics = "${app.config.input-topic}", containerFactory = "kafkaListenerContainerFactory")
    public void processPayload(ConsumerRecords<String, String> records, Acknowledgment ack)  {
        long startTime = System.currentTimeMillis();
        try {
            List<String> payloads = new ArrayList<String>();
            for (ConsumerRecord<String, String> record : records) {
                payloads.add(record.key());
            }
            LOGGER.info("Received the primary keys {} ",payloads.size() );

            List<List<String>> subLists = ListUtils.partition(payloads, 10);
            for (List<String> subList : subLists) {
                for (Map.Entry<String, Map<String, String>> entry : applicationConfig.getQueries().entrySet()) {
                    sinkService.selectAndInsert(entry.getKey(), entry.getValue(), subList);
                }
            }
            LOGGER.info("COMPLETED INSERTING SUCCESSFULLY {} rows IN {} ms ", (System.currentTimeMillis()-startTime));
            ack.acknowledge();
        } catch (Exception e){
            LOGGER.error("Exception occurred in processPayload ", e);
        }
    }
}
