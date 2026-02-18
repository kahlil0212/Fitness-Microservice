import { Box, Button, Typography } from "@mui/material";


const WelcomePage = ({ handleLogin }) => {


    return (
        <Box sx={{
            background: "#F0F0F0",
            height: "100vh",
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            justifyContent: "center",
            textAlign: "center"
        }}>
            <Typography variant="h4" gutterBottom>Welcome to the Fitness Tracker App</Typography>
            <Typography variant="subtitle1" sx={{ mb: 3 }}>Please login to access your activities</Typography>
            <Button variant="contained" color="primary" size="large"
                onClick={handleLogin}>Login</Button>
        </Box>
    )
}

export default WelcomePage;