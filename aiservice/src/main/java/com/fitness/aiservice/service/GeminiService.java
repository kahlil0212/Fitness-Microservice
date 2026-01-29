package com.fitness.aiservice.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class GeminiService {


    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public GeminiService(WebClient.Builder webClientBuilder){
        this.webClient = webClientBuilder.build();
    }

    public String getAnswers(String prompt){
        Map<String,Object> requestBody = Map.of(
                "contents",new Object[]{
                Map.of("parts",new Object[]{
                        Map.of("text",prompt)
                })
        });

        String response = webClient.post()
                .uri(geminiApiUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header("x-goog-api-key",geminiApiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return response;
    }
}
