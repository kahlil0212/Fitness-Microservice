import { AppBar, Box, Container, Toolbar, Button, Menu, MenuItem, IconButton } from "@mui/material";
import { Link } from "react-router";
import { FitnessCenter, AccountCircle } from "@mui/icons-material";
import { useState } from "react";

/**
 * NavBar Component
 *
 * Navigation bar with fitness logo, navigation links, and user menu.
 * Provides navigation for authenticated users and handles logout functionality.
 *
 * @param {Function} logout - Function to handle user logout
 */
const NavBar = ({ logout }) => {
    // State for user menu dropdown
    const [anchorEl, setAnchorEl] = useState(null);
    const open = Boolean(anchorEl);

    /**
     * Opens the user menu dropdown
     * @param {Event} event - Click event from the menu button
     */
    const handleMenu = (event) => {
        setAnchorEl(event.currentTarget);
    };

    /**
     * Closes the user menu dropdown
     */
    const handleClose = () => {
        setAnchorEl(null);
    };

    return (
        <AppBar position="static">
            <Container maxWidth="xl">
                <Toolbar disableGutters>
                    {/* Fitness logo - links to home page */}
                    <IconButton edge="start" color="inherit" component={Link} to="/">
                        <FitnessCenter />
                    </IconButton>

                    {/* Main navigation links */}
                    <Box sx={{ flexGrow: 1, display: { xs: 'none', md: 'flex' }, ml: 2 }}>
                        <Button color="inherit" component={Link} to="/progress">
                            Progress
                        </Button>
                        <Button color="inherit" component={Link} to="/activities">
                            Activities
                        </Button>
                    </Box>

                    {/* User account menu */}
                    <div>
                        <IconButton
                            size="large"
                            aria-label="account of current user"
                            aria-controls="menu-appbar"
                            aria-haspopup="true"
                            onClick={handleMenu}
                            color="inherit"
                        >
                            <AccountCircle />
                        </IconButton>
                        <Menu
                            id="menu-appbar"
                            anchorEl={anchorEl}
                            anchorOrigin={{
                                vertical: 'top',
                                horizontal: 'right',
                            }}
                            keepMounted
                            transformOrigin={{
                                vertical: 'top',
                                horizontal: 'right',
                            }}
                            open={open}
                            onClose={handleClose}
                        >
                            <MenuItem onClick={handleClose} component={Link} to="/profile">Profile</MenuItem>
                            <MenuItem onClick={handleClose} component={Link} to="/settings">Settings</MenuItem>
                            {/* Logout menu item - closes menu and calls logout function */}
                            <MenuItem onClick={() => { handleClose(); logout(); }}>Logout</MenuItem>
                        </Menu>
                    </div>
                </Toolbar>
            </Container>
        </AppBar >
    )
}


export default NavBar;