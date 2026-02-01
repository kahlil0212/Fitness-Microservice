import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App.jsx'
import { Provider } from 'react-redux'
import { store } from './store/store.jsx'
import { AuthProvider } from 'react-oauth2-code-pkce'
import { authConfig } from './authentication/authConfig.jsx'

createRoot(document.getElementById('root')).render(

  <AuthProvider authConfig={authConfig}>
    <Provider store={store}>
      <App />
    </Provider>
  </AuthProvider>,
)
