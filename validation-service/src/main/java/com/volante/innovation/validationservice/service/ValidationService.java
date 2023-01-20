package com.volante.innovation.validationservice.service;


import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.kafka.streams.KafkaStreamsTracing;
import brave.propagation.CurrentTraceContext;
import brave.propagation.TraceContext;
import com.volante.innovation.validationservice.bindings.KafkaListenerBinding;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.kafka.support.KafkaSendFailureException;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@EnableBinding(KafkaListenerBinding.class)
public class ValidationService {

    @Autowired
    private Tracing tracing;


    @ServiceActivator(inputChannel = "group.validate.errors")

    public void errorHandler(ErrorMessage em) {
        log.debug("got error message over errorChannel: {}", em);
        if (null != em.getPayload() && em.getPayload() instanceof KafkaSendFailureException) {
            KafkaSendFailureException kafkaSendFailureException = (KafkaSendFailureException) em.getPayload();
            if (kafkaSendFailureException.getRecord() != null && kafkaSendFailureException.getRecord().value() != null
                    && kafkaSendFailureException.getRecord().value() instanceof byte[]) {

                TraceContext.Extractor<Headers> extractor = tracing.propagation()
                        .extractor((request, key) -> new String(request.lastHeader(key).value()));
                Span span = tracing.tracer().joinSpan(extractor.extract(kafkaSendFailureException.getRecord().headers()).context());
                try (Tracer.SpanInScope ignored = tracing.tracer().withSpanInScope(span.start())) {


                    log.warn("error channel message. Payload {}", new String((byte[]) (kafkaSendFailureException.getRecord().value())));

                } finally {
                    span.finish();
                }

            }
        }
    }

    @Autowired
    KafkaStreamsTracing kafkaStreamsTracing;

    @StreamListener("input-channel")
    @SendTo("output-channel")
    public KStream<String, String> process(KStream<String, String> input) {

        input.process(
                kafkaStreamsTracing.foreach("spandemo",(k,v)->{

                    log.info("hello " + k + " " + v);

                    Span span = tracing.tracer().nextSpan();
                    try (Tracer.SpanInScope ignored = tracing.tracer().withSpanInScope(span.start())) {

                        span.tag("error", "true");
                    } finally {
                        span.finish();
                    }

                }));

        return input;
    }
}
