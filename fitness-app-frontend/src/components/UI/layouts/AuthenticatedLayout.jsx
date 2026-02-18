import { Box, Button } from "@mui/material";
import { Outlet } from "react-router";
import NavBar from "../NavBar";

/**
 * Authenticated Layout Component
 *
 * Layout wrapper for authenticated users. Provides the navigation bar,
 * logout button, and renders child routes via React Router's Outlet.
 *
 * @param {Function} onLogout - Function to handle user logout
 */
const AuthenticatedLayout = ({ onLogout }) => {
    return (
        <Box component="section" sx={{ background: "#F0F0F0", height: "100vh", p: 2, border: '1px dashed grey' }}>
            {/* Navigation bar with logout functionality */}
            <NavBar logout={onLogout} />
            {/* Additional logout button for convenience */}
            <Button variant="contained" color="secondary" onClick={onLogout}>
                Logout
            </Button>
            {/* Render child routes (ActivitiesPage, ActivityDetail, etc.) */}
            <Outlet />
        </Box>
    );
};

export default AuthenticatedLayout;