
import { Box, Button } from "@mui/material"
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

  useEffect(() => {
    if (token) {
      dispatch(setCredentials({ token, user: tokenData }))
      setAuthReady(true);
    }
  }, [token, tokenData, dispatch])
  return (
    <Router>
      {!token ? (
        <Button variant="contained" color="primary"
          onClick={handleLogin}>Login</Button>)
        : (
          <Box component="section" sx={{ p: 2, border: '1px dashed grey' }}>
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
