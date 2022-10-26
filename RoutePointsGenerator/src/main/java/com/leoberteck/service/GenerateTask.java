package com.leoberteck.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leoberteck.bean.Route;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GenerateTask {

    private static final Logger logger = LoggerFactory.getLogger(GenerateTask.class);
    private final String outputTopic;
    private final Route route;
    private final Integer pointGenerationIntervalMs;
    private final Producer<String, String> producer;
    private final ScheduledExecutorService executor;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Integer currentIndex = 0;
    private CompletableFuture<Void> completed = new CompletableFuture<>();
    private ScheduledFuture<?> scheduledFuture;

    private static final String ROUTE_START="ROUTE_START";
    private static final String ROUTE_INPROGRESS="ROUTE_INPROGRESS";
    private static final String ROUTE_FINISH="ROUTE_FINISH";
    public GenerateTask(String outputTopic, Route route, Integer pointGenerationIntervalMs, Producer<String, String> producer, ScheduledExecutorService executor) {
        this.outputTopic = outputTopic;
        this.route = route;
        this.pointGenerationIntervalMs = pointGenerationIntervalMs;
        this.producer = producer;
        this.executor = executor;
    }

    public CompletableFuture<Void> run(){
        scheduledFuture = executor.scheduleAtFixedRate(sendPointRunnable, 0, pointGenerationIntervalMs, TimeUnit.MILLISECONDS);
        return completed;
    }

    private final Runnable sendPointRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                var status = ROUTE_INPROGRESS;
                if( currentIndex == 0 ){
                    status = ROUTE_START;
                }
                if(currentIndex + 1 == route.getFeatures().get(0).getGeometry().getCoordinates().size()){
                    status = ROUTE_FINISH;
                }
                var payload = objectMapper.writeValueAsString(
                    new RoutePoint(
                        route.getMetadata().getName(), status,
                        route.getFeatures().get(0).getGeometry().getCoordinates().get(currentIndex)
                    ));
                producer.send(new ProducerRecord<>(outputTopic, payload));
                logger.info(payload);
                currentIndex++;
                if(status.equals(ROUTE_FINISH)){
                    scheduledFuture.cancel(false);
                    completed.complete(null);
                    logger.info("finished");
                }
            } catch (Exception e) {
                logger.error("Failed to send point", e);
            }
        }
    };

    private static final class RoutePoint {
        @JsonProperty("route_name")
        private String routeName;
        @JsonProperty("route_status")
        private String routeStatus;
        @JsonProperty("current_point")
        private Route.Coordinate currentPoint;

        public RoutePoint() {
        }

        public RoutePoint(String routeName, String routeStatus, Route.Coordinate currentPoint) {
            this.routeName = routeName;
            this.routeStatus = routeStatus;
            this.currentPoint = currentPoint;
        }

        public String getRouteName() {
            return routeName;
        }

        public void setRouteName(String routeName) {
            this.routeName = routeName;
        }

        public Route.Coordinate getCurrentPoint() {
            return currentPoint;
        }

        public void setCurrentPoint(Route.Coordinate currentPoint) {
            this.currentPoint = currentPoint;
        }

        public String getRouteStatus() {
            return routeStatus;
        }

        public void setRouteStatus(String routeStatus) {
            this.routeStatus = routeStatus;
        }
    }
}
