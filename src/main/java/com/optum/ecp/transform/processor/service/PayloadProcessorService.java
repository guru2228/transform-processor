package com.optum.ecp.transform.processor.service;

import com.optum.ecp.transform.processor.config.ApplicationConfig;
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
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class PayloadProcessorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PayloadProcessorService.class);

    SinkService sinkService;

    ApplicationConfig applicationConfig;

    public PayloadProcessorService(SinkService sinkService, ApplicationConfig applicationConfig) {
        this.sinkService = sinkService;
        this.applicationConfig = applicationConfig;
    }

    @KafkaListener(topics = "${app.config.input-topic}")
    public void processPayload(ConsumerRecords<String, String> records, Acknowledgment ack) {

        Set<Map.Entry<String, Map<String, String>>> entries = applicationConfig.getQueries().entrySet();

        ExecutorService executorService = Executors.newFixedThreadPool(entries.size());

        CountDownLatch latch = new CountDownLatch(entries.size());

        long startTime = System.currentTimeMillis();

        try {
            List<String> payloads = new ArrayList<String>();
            for (ConsumerRecord<String, String> record : records) {
                payloads.add(record.key());
            }
            LOGGER.info("Received the primary keys {} ", payloads.size());

            // todo: increase in increments of 128 for additional testing
            List<List<String>> subLists = ListUtils.partition(payloads, 128);
            for (Map.Entry<String, Map<String, String>> entry : entries) {

                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        for (List<String> subList : subLists) {
                            String table = entry.getKey();
                            sinkService.selectAndInsert(table, entry.getValue(), subList);
                        }
                        latch.countDown();
                    }
                });

            }

            try {
                latch.await();
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }

            LOGGER.info("COMPLETED INSERTING SUCCESSFULLY {} rows IN {} ms ", (System.currentTimeMillis() - startTime));
            ack.acknowledge();


        } catch (Exception e) {
            LOGGER.error("Exception occurred in processPayload ", e);
        } finally {
            executorService.shutdown();
        }
    }
}
