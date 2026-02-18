import { Outlet } from "react-router";

/**
 * Unauthenticated Layout Component
 *
 * Minimal layout wrapper for unauthenticated users.
 * Simply renders child routes via React Router's Outlet.
 * Currently just shows the WelcomePage for login.
 *
 * @param {Function} onLogin - Function to handle user login (passed to child components)
 */
const UnauthenticatedLayout = ({ onLogin }) => {
    return <Outlet />;
};

export default UnauthenticatedLayout;