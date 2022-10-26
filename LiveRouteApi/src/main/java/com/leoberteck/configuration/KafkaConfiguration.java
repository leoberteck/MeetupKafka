package com.leoberteck.configuration;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.internals.ConsumerFactory;
import reactor.kafka.receiver.internals.DefaultKafkaReceiver;

import java.util.Properties;
import java.util.regex.Pattern;

@Configuration
public class KafkaConfiguration {
    private final ReceiverConfigurationProperties receiverConfigurationProperties;

    @Value("${app.config.input_topic}")
    private String inputTopic;

    public KafkaConfiguration(ReceiverConfigurationProperties receiverConfigurationProperties) {
        this.receiverConfigurationProperties = receiverConfigurationProperties;
    }

    @Bean
    public KafkaReceiver<String, String> kafkaReceiver() {
        Properties configProps = new Properties();
        configProps.putAll(receiverConfigurationProperties);

        return new DefaultKafkaReceiver<>(ConsumerFactory.INSTANCE,
            ReceiverOptions.<String, String>create(configProps).subscription(Pattern.compile(inputTopic))
        );
    }
}
