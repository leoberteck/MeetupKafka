package com.leoberteck.handler;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;

import javax.annotation.PostConstruct;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Service
public class KafkaHandler {
    private static final Logger logger = LoggerFactory.getLogger(KafkaHandler.class);
    private final KafkaReceiver<String, String> kafkaReceiver;
    private Flux<String> routePointsStream;

    public KafkaHandler(KafkaReceiver<String, String> kafkaReceiver) {
        this.kafkaReceiver = kafkaReceiver;
    }

    @PostConstruct
    private void init(){
        routePointsStream = kafkaReceiver.receive()
            .publish()
            .autoConnect(1)
            .onBackpressureBuffer(100, BufferOverflowStrategy.DROP_OLDEST)
            .map(ConsumerRecord::value);
    }

    public Mono<ServerResponse> getPointsStream(ServerRequest serverRequest) {
        return ok().contentType(MediaType.APPLICATION_NDJSON).body(routePointsStream, JsonNode.class);
    }
}
