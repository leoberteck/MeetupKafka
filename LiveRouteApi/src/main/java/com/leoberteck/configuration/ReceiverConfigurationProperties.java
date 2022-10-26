package com.leoberteck.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
@ConfigurationProperties(prefix = "app.config.kafka")
public class ReceiverConfigurationProperties extends HashMap<String, Object> {
}
