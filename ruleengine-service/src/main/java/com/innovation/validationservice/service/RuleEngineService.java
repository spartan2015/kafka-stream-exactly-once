package com.innovation.validationservice.service;


import com.innovation.validationservice.bindings.KafkaListenerBinding;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@EnableBinding(KafkaListenerBinding.class)
public class RuleEngineService {

    @StreamListener("input-channel")
    @SendTo("output-channel")
    public KStream<String, String> process(KStream<String, String> input) {

        input.foreach((k, v) -> log.info("We have a message in ruleengine stream: {} {}", k, v));

        return input;
    }
}
