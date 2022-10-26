package com.leoberteck;

import com.leoberteck.service.GenerateTask;
import com.leoberteck.service.RouteLoader;
import com.leoberteck.service.TopicManager;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.Serdes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

/**
 * Hello world!
 */
@SpringBootApplication
public class App implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args).close();
    }

    private final Logger logger = LoggerFactory.getLogger(App.class);
    private final RouteLoader routeLoader;
    private final TopicManager topicManager;
    private final ConfigurableApplicationContext context;

    @Value("${app.config.routesPath}")
    private String routesPath;
    @Value("${app.config.numberOfVehicles}")
    private Integer numberOfVehicles;
    @Value("${app.config.pointGenerationIntervalMs}")
    private Integer pointGenerationIntervalMs;
    @Value("${app.kafka.bootstrapServers}")
    private String bootstrapServers;
    @Value("${app.kafka.outputTopic.name}")
    private String outputTopic;
    @Value("${app.kafka.outputTopic.partitions}")
    private Integer outputTopicPartitions;
    @Value("${app.kafka.outputTopic.replication}")
    private Short outputTopicReplication;
    @Value("${app.kafka.outputTopic.properties.retentionMs}")
    private String retentionMs;
    @Value("${app.kafka.outputTopic.properties.retentionBytes}")
    private String retentionBytes;
    @Value("${app.kafka.outputTopic.properties.cleanupPolicy}")
    private String cleanupPolicy;


    @Autowired
    public App(RouteLoader routeLoader, TopicManager topicManager, ConfigurableApplicationContext context) {
        this.routeLoader = routeLoader;
        this.topicManager = topicManager;
        this.context = context;
    }

    @Override
    public void run(String... args) throws Exception {
        topicManager.createIfNotExists(
            outputTopic,
            outputTopicPartitions,
            outputTopicReplication,
            new HashMap<>() {{
                put("retention.ms", retentionMs);
                put("retention.bytes", retentionBytes);
                put("cleanup.policy", cleanupPolicy);
            }}
        );
        var r = routeLoader.load("classpath:routes.json");
        logger.info(String.valueOf(r));
        var producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", bootstrapServers);
        var producer = new KafkaProducer<>(
                producerProperties,
                Serdes.String().serializer(),
                Serdes.String().serializer()
        );

        var executor = Executors.newScheduledThreadPool(1);
        var listTasks = new ArrayList<CompletableFuture<Void>>();
        for (var x = 0; x < r.size(); x++) {
            listTasks.add(new GenerateTask(outputTopic, r.get(x), pointGenerationIntervalMs, producer, executor).run());
        }
        listTasks.forEach(CompletableFuture::join);
        logger.info("All tasks finished");
        System.exit(SpringApplication.exit(context));
    }
}
