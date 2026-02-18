import { Navigate } from "react-router";
import ActivitiesPage from "../pages/ActivitiesPage";
import ActivityDetail from "../components/ActivityDetail";
import WelcomePage from "../pages/WelcomePage";
import AuthenticatedLayout from "../components/UI/layouts/AuthenticatedLayout";
import UnauthenticatedLayout from "../components/UI/layouts/UnauthenticatedLayout";

/**
 * Route Configuration Function
 *
 * Defines the application's routing structure based on authentication status.
 * Uses React Router's nested routing with layouts to conditionally render
 * different UI components for authenticated vs unauthenticated users.
 *
 * @param {boolean} isAuthenticated - Whether the user is logged in
 * @param {Function} handleLogin - Function to trigger login
 * @param {Function} handleLogout - Function to trigger logout
 * @returns {Array} Route configuration array for useRoutes hook
 */
export const routes = (isAuthenticated, handleLogin, handleLogout) => [
    {
        path: "/",
        // Conditionally render layout based on authentication status
        element: isAuthenticated ? (
            <AuthenticatedLayout onLogout={handleLogout} />
        ) : (
            <UnauthenticatedLayout onLogin={handleLogin} />
        ),
        children: isAuthenticated ? [
            // Authenticated user routes
            {
                index: true,
                // Redirect root to activities page for logged-in users
                element: <Navigate to="/activities" replace />
            },
            {
                path: "activities",
                element: <ActivitiesPage />
            },
            {
                path: "activities/:id",
                element: <ActivityDetail />
            }
        ] : [
            // Unauthenticated user routes
            {
                index: true,
                element: <WelcomePage handleLogin={handleLogin} />
            },
            {
                // Catch-all route for unauthenticated users
                // Redirects any unmatched routes to the welcome page
                path: "*",
                element: <Navigate to="/" replace />
            }
        ]
    }
];