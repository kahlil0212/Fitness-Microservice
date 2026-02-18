
import { BrowserRouter as Router } from "react-router";
import AppContent from "./components/AppContent";

/**
 * Main Application Component
 *
 * Root component that provides React Router context to the entire application.
 * The AppContent component handles all authentication logic and routing decisions.
 */
const App = () => {
  return (
    <Router>
      <AppContent />
    </Router>
  );
}

export default App;
