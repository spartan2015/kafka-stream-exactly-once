package com.innovation.validationservice;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.kafka.streams.KafkaStreamsTracing;
import brave.propagation.TraceContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.config.StreamsBuilderFactoryBeanConfigurer;

@Configuration
@Slf4j
public class KafkaStreamsConfig {

    @Bean
    public KafkaStreamsTracing kafkaStreamsTracer(@Autowired Tracing tracing) {
        return KafkaStreamsTracing.create(tracing);
    }

   /* @Bean
    public StreamsBuilderFactoryBean privateStreamBuilder(
            @Autowired KafkaProperties kafkaProperties,
            @Autowired KafkaStreamsTracing kafkaStreamsTracing) {

        KafkaStreamsConfiguration streamsConfiguration = configureStream(kafkaProperties.buildStreamsProperties());
        StreamsBuilderFactoryBean streamsBuilderFactoryBean = new StreamsBuilderFactoryBean(streamsConfiguration);
        streamsBuilderFactoryBean.setClientSupplier(kafkaStreamsTracing.kafkaClientSupplier());
        return  streamsBuilderFactoryBean;
    }*/
/*
    @Bean
    public StreamsBuilderFactoryBeanConfigurer streamsCustomizer() {
        return new StreamsBuilderFactoryBeanConfigurer() {
            @Override
            public void configure(StreamsBuilderFactoryBean factoryBean) {
                factoryBean.setStreamsUncaughtExceptionHandler(getStreamsUncaughtExceptionHandler());
            }
            @Override
            public int getOrder() {
                return Integer.MAX_VALUE - 10000;
            }
        };
    }*/

    /*private StreamsUncaughtExceptionHandler getStreamsUncaughtExceptionHandler() {
        return exception -> {
            Throwable cause = exception.getCause();

            TraceContext.Extractor<Headers> extractor = tracing.propagation()
                    .extractor((request, key) -> new String(request.lastHeader(key).value()));


            Span span = tracing.tracer().nextSpan();

            try (Tracer.SpanInScope ignored = tracing.tracer().withSpanInScope(span.start())) {

                if (cause.getClass().equals(Exception.class)) {
                    log.error(cause.getMessage());
                    return StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse.REPLACE_THREAD;
                }

            } finally {
                span.finish();
            }

            return StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse.REPLACE_THREAD;
        };
    }*/

}
