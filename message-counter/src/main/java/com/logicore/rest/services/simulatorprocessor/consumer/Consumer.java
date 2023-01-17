package com.logicore.rest.services.simulatorprocessor.consumer;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class Consumer {

    private AtomicLong counter = new AtomicLong(0);

    @KafkaListener(topics = {"messageprocessed"})
    public void onMessageRest(ConsumerRecord<String, String> customerRecord) {
        counter.getAndIncrement();
        log.info("Number of kafka messages received: {} ", String.valueOf(counter));
    }
}
