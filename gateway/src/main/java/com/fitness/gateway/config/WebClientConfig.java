package com.fitness.gateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced //allows webclient to resolve service name via eureka server
    public WebClient.Builder webClientBuilder(){

        return WebClient.builder();
    }
    @Bean
    public WebClient userServiceWebClient(WebClient.Builder webClientBuilder){
        return webClientBuilder.baseUrl("http://USER-SERVICE")
                .build();
    }
}
