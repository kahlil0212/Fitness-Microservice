
import { Box, Button, Typography } from "@mui/material"
import { useEffect, useState } from "react";
import { useAuthContext } from "react-oauth2-code-pkce"
import { useDispatch } from "react-redux";
import { Route, BrowserRouter as Router, Routes } from "react-router"
import { setCredentials } from "./store/authSlice";
import ActivitiesPage from "./pages/ActivitiesPage";
import ActivityDetail from "./components/ActivityDetail";
import { Navigate } from "react-router";

function App() {

  const { token, tokenData, logIn, logOut } = useAuthContext();
  const dispatch = useDispatch();
  const [authReady, setAuthReady] = useState(false);

  const handleLogin = () => {
    logIn();
  }
  const handleLogout = () => {
    logOut();
  }

  useEffect(() => {
    if (token) {
      dispatch(setCredentials({ token, user: tokenData }))
      setAuthReady(true);
    }
  }, [token, tokenData, dispatch])
  return (
    <Router>
      {!token ? (
        <Box sx={{ height: "100vh", display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", textAlign: "center" }}>
          <Typography variant="h4" gutterBottom>Welcome to the Fitness Tracker App</Typography>
          <Typography variant="subtitle1" sx={{ mb: 3 }}>Please login to access your activities</Typography>
          <Button variant="contained" color="primary" size="large"
            onClick={handleLogin}>Login</Button>
        </Box>
      )
        : (
          <Box component="section" sx={{ p: 2, border: '1px dashed grey' }}>
            <Button variant="contained" color="secondary"
              onClick={handleLogout}>Logout</Button>
            <Routes>
              <Route path="/activities" element={<ActivitiesPage />} />
              <Route path="/activities/:id" element={<ActivityDetail />} />
              <Route path="/" element={token ? <Navigate to={"/activities"} replace /> : <div>Welcome! Please Login</div>} />
            </Routes>
          </Box>
        )}
    </Router>
  )
}

export default App
