package com.fitness.gateway.filter;

import com.fitness.gateway.model.RegisterRequest;
import com.fitness.gateway.service.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeyCloakUserSyncFilter implements WebFilter {

    private final UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {

        String token = serverWebExchange.getRequest().getHeaders().getFirst("Authorization");
        String userId = serverWebExchange.getRequest().getHeaders().getFirst("X-USER-ID");
        RegisterRequest registerRequest = getUserDetails(token);

        if(userId == null){
            userId = registerRequest.getKeyCloakId();
        }

        if(userId != null && token != null) {
            String finalUserId = userId;
            return userService.validateUser(userId)
                    .flatMap(exist -> {
                        if(!exist){
                            //Register User

                            if(registerRequest != null) {
                                return userService.registerUser(registerRequest)
                                        .then(Mono.empty());
                            }else{
                                return Mono.empty();
                            }
                        }else{
                            log.info("User already exists. Skipping sync");
                            return Mono.empty();
                        }
                    }).then(Mono.defer(() -> {
                        ServerHttpRequest mutatedRequest = serverWebExchange.getRequest().mutate()
                                .header("X-USER-ID", finalUserId)
                                .build();
                        return webFilterChain.filter(serverWebExchange.mutate().request(mutatedRequest).build());
                    }));
        }
        return webFilterChain.filter(serverWebExchange); //if user id or token is missing just continue with filter chain
    }

    private RegisterRequest getUserDetails(String token) {

        try{
            String tokenWithoutBearer = token.split("Bearer ")[1];
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setKeyCloakId(claimsSet.getSubject());
            registerRequest.setEmail(claimsSet.getStringClaim("email"));
            registerRequest.setFirstName(claimsSet.getStringClaim("given_name"));
            registerRequest.setLastName(claimsSet.getStringClaim("family_name"));
            registerRequest.setPassword("dummyPassword");
            return registerRequest;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
