package com.fitness.gateway.service;

import com.fitness.gateway.model.RegisterRequest;
import com.fitness.gateway.model.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final WebClient userServiceWebClient;

    public Mono<Boolean> validateUser(String userId) {

        log.info("Calling validate user for userId {} ", userId);


            return userServiceWebClient.get()
                    .uri("/api/users/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .onErrorResume(WebClientResponseException.class, ex -> {
                        if (ex.getStatusCode() == HttpStatus.NOT_FOUND)
                            return Mono.error(new RuntimeException("User not found: " + userId));
                         else if (ex.getStatusCode() == HttpStatus.BAD_REQUEST)
                            return Mono.error( new RuntimeException("Invalid Request: " + userId));
                         return Mono.error(new RuntimeException("Unexpected error: " + ex.getMessage()));
                    });
    }

    public Mono<UserResponse> registerUser(RegisterRequest request) {

        log.info("Calling User Registration for email {} ", request.getEmail());


        return userServiceWebClient.post()
                .uri("/api/users/register")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode() == HttpStatus.BAD_REQUEST)
                        return Mono.error( new RuntimeException("Bad Request: " + ex.getMessage()));
                    else if(ex.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR)
                        return Mono.error( new RuntimeException("Internal server error: " + ex.getMessage() ));
                    return Mono.error(new RuntimeException("Unexpected error: " + ex.getMessage()));
                });
    }
}
