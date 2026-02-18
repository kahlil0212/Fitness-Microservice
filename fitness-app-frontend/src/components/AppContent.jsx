import { useEffect, useState } from "react";
import { useAuthContext } from "react-oauth2-code-pkce";
import { useDispatch } from "react-redux";
import { useRoutes, useNavigate } from "react-router";
import { setCredentials, logout } from "../store/authSlice";
import { routes } from "../routing/routes";

/**
 * AppContent Component
 *
 * Main application component that handles authentication state and routing.
 * This component is wrapped by the Router in App.jsx and uses React Router's
 * useRoutes hook to render the appropriate components based on authentication status.
 */
const AppContent = () => {
    // Authentication context from react-oauth2-code-pkce
    const { token, tokenData, logIn, logOut } = useAuthContext();
    const dispatch = useDispatch();
    const navigate = useNavigate();

    // Local state to track if user is authenticated and ready to use the app
    const [authReady, setAuthReady] = useState(false);

    /**
     * Handles user login by calling the auth context's logIn method
     * This typically redirects to the OAuth provider (Keycloak)
     */
    const handleLogin = () => {
        logIn();
    };

    /**
     * Handles user logout by:
     * 1. Clearing Redux state and localStorage
     * 2. Clearing the auth context token
     * 3. Navigating to the home page (welcome page for unauthenticated users)
     */
    const handleLogout = () => {
        // Clear Redux store and localStorage
        dispatch(logout());
        // Clear auth context token
        logOut();
        // Navigate to home page
        navigate("/", { replace: true });
    };

    /**
     * Effect that monitors authentication token changes
     * When token exists: stores credentials in Redux and sets authReady to true
     * When token is null/undefined: sets authReady to false (user is logged out)
     */
    useEffect(() => {
        if (token) {
            dispatch(setCredentials({ token, user: tokenData }));
            setAuthReady(true);
        } else {
            setAuthReady(false);
        }
    }, [token, tokenData, dispatch]);

    // Generate routes based on authentication status
    const routing = useRoutes(routes(authReady, handleLogin, handleLogout));

    return routing;
}

export default AppContent;