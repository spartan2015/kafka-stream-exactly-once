package com.volante.innovation.validationservice.bindings;


import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;

public interface KafkaListenerBinding {

    @Input("input-channel")
    KStream<String, String> inputStream();

    @Output("output-channel")
    KStream<String, String> outputStream();
}
