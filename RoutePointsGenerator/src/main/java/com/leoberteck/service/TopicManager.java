package com.leoberteck.service;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class TopicManager {
    private static final Logger logger = LoggerFactory.getLogger(TopicManager.class);
    private AdminClient adminClient;

    @Value("${app.kafka.bootstrapServers}")
    private String bootstrapServers;

    @PostConstruct
    private void init(){
        var props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        this.adminClient = AdminClient.create(props);
    }

    public void createIfNotExists(String topicName, Integer partitions, Short replication, Map<String, String> properties) {
        var topic = new NewTopic(topicName, partitions, replication);
        topic.configs(properties);
        try {
            adminClient.createTopics(Collections.singletonList(topic)).all().get();
            logger.info("Topic {}, Created", topicName);
        } catch (Exception e) {
            logger.warn("Topic {}, Already Exists", topicName, e);
        }
    }
}
