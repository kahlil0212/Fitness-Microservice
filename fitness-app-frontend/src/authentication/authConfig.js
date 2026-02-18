
/**
 * OAuth 2.0 PKCE Authentication Configuration
 *
 * Configuration object for the react-oauth2-code-pkce library.
 * This sets up OAuth 2.0 authentication with Keycloak as the identity provider.
 * Uses Proof Key for Code Exchange (PKCE) for enhanced security.
 */
export const authConfig = {
    /**
     * OAuth 2.0 Client ID
     * Must match the client ID configured in Keycloak
     */
    clientId: 'oauth2-pkce-client',

    /**
     * Authorization Endpoint
     * Keycloak's OAuth 2.0 authorization endpoint URL
     * Users are redirected here to authenticate
     */
    authorizationEndpoint: 'http://localhost:8181/realms/fitness-oauth2/protocol/openid-connect/auth',

    /**
     * Token Endpoint
     * Keycloak's OAuth 2.0 token endpoint URL
     * Used to exchange authorization codes for access tokens
     */
    tokenEndpoint: 'http://localhost:8181/realms/fitness-oauth2/protocol/openid-connect/token',

    /**
     * Redirect URI
     * Where Keycloak redirects users after authentication
     * Must match the redirect URI configured in Keycloak client settings
     */
    redirectUri: 'http://localhost:5173',

    /**
     * OAuth 2.0 Scopes
     * Requested permissions from the identity provider
     * - openid: Required for OpenID Connect
     * - profile: Access to user profile information
     * - email: Access to user email address
     * - offline_access: Allows refresh tokens for long-term sessions
     */
    scope: 'openid profile email offline_access',

    /**
     * Refresh Token Expiration Handler
     * Called when the refresh token expires
     * Automatically triggers re-authentication
     */
    onRefreshTokenExpire: (event) => event.logIn(),
}