package com.leoberteck.configuration;

import com.leoberteck.handler.KafkaHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Configuration
public class RouteConfiguration {
    @Bean
    public RouterFunction<ServerResponse> streamRoutes(KafkaHandler handler) {
        return RouterFunctions
            .route(GET("/routes/stream/points"), handler::getPointsStream);
    }

    @Bean
    public RouterFunction<ServerResponse> htmlRouter(@Value("classpath:/static/index.html") Resource html) {
        return RouterFunctions.route(GET("/"), request -> ok().contentType(MediaType.TEXT_HTML).bodyValue(html)
        );
    }
}
