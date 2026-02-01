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

/**
 * A WebFilter that synchronizes users from Keycloak into the local database.
 *
 * <p>This filter intercepts every incoming request to the API gateway and performs
 * the following steps:</p>
 * <ol>
 *   <li>Extracts the JWT token from the {@code Authorization} header</li>
 *   <li>Extracts or derives the user ID from the {@code X-USER-ID} header or the JWT token</li>
 *   <li>Checks if the user exists in the local database</li>
 *   <li>Registers the user if they do not already exist</li>
 *   <li>Mutates the request to include the {@code X-USER-ID} header for downstream services</li>
 * </ol>
 *
 * <p>This ensures that any authenticated user is automatically synced into the local
 * database on their first request, without requiring a separate registration flow.</p>
 *
 * <p><b>Example flow:</b></p>
 * <pre>
 *   Request → Extract Token → Validate User → Register (if new) → Add X-USER-ID Header → Route to Service
 * </pre>
 *
 * @see UserService
 * @see RegisterRequest
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class KeyCloakUserSyncFilter implements WebFilter {

    private final UserService userService;

    /**
     * Intercepts incoming requests, syncs the user with the local database if necessary,
     * and forwards the request with the {@code X-USER-ID} header attached.
     *
     * <p>If the {@code Authorization} token or user ID is missing, the filter skips
     * the sync logic and passes the request through unchanged.</p>
     *
     * @param serverWebExchange the current server web exchange, containing the request and response
     * @param webFilterChain    the downstream filter chain to continue processing
     * @return a {@link Mono<Void>} that completes when the filter chain has finished processing
     */
    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {

        // Extract the JWT token from the Authorization header
        String token = serverWebExchange.getRequest().getHeaders().getFirst("Authorization");

        // Extract the X-USER-ID header if it was already set by a previous request
        String userId = serverWebExchange.getRequest().getHeaders().getFirst("X-USER-ID");

        // Decode the JWT token and extract user details (Keycloak ID, name, email, etc.)
        RegisterRequest registerRequest = getUserDetails(token);

        // If X-USER-ID header wasn't provided, fall back to the Keycloak ID from the token
        if (userId == null) {
            userId = registerRequest.getKeyCloakId();
        }

        // Only proceed with sync logic if both userId and token are present
        if (userId != null && token != null) {
            // Copy userId to a final variable so it can be used inside the lambda
            // Java lambdas require variables to be effectively final
            String finalUserId = userId;

            // Check if the user already exists in our database
            return userService.validateUser(userId)
                    .flatMap(exist -> {
                        if (!exist) {
                            // User does not exist - register them if we have their details from the token
                            if (registerRequest != null) {
                                return userService.registerUser(registerRequest)
                                        .then(Mono.empty()); // Registration complete, emit nothing and move on
                            } else {
                                // No user details available to register, skip
                                return Mono.empty();
                            }
                        } else {
                            // User already exists in the database, no action needed
                            log.info("User already exists. Skipping sync");
                            return Mono.empty();
                        }
                    }).then(Mono.defer(() -> {
                        // After sync logic completes, mutate the request to add X-USER-ID header
                        // This allows downstream microservices to know who the user is
                        // without having to decode the JWT token themselves
                        ServerHttpRequest mutatedRequest = serverWebExchange.getRequest().mutate()
                                .header("X-USER-ID", finalUserId)
                                .build();

                        // Pass the mutated request down the filter chain to the next filter or route
                        return webFilterChain.filter(serverWebExchange.mutate().request(mutatedRequest).build());
                    }));
        }

        // If userId or token is missing, skip sync logic and continue the filter chain as normal
        return webFilterChain.filter(serverWebExchange);
    }

    /**
     * Decodes the JWT token and extracts user details to populate a {@link RegisterRequest}.
     *
     * <p>The token is expected to be a Bearer token from Keycloak containing claims such as
     * the user's Keycloak ID, name, and email.</p>
     *
     * @param token the raw JWT token string from the {@code Authorization} header
     * @return a {@link RegisterRequest} populated with the user's details extracted from the token,
     *         or {@code null} if the token is invalid or cannot be decoded
     */
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
