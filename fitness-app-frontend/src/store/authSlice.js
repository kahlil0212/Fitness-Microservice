import { createSlice } from '@reduxjs/toolkit'

/**
 * Authentication Redux Slice
 *
 * Manages authentication state including user data, tokens, and user ID.
 * Persists authentication data in localStorage for session persistence.
 */
export const authSlice = createSlice({
  name: 'auth',
  initialState: {
    // Initialize state from localStorage to persist sessions across browser refreshes
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('token') || null,
    userId: localStorage.getItem('userId') || null
  },
  reducers: {
    /**
     * Sets user credentials in Redux state and localStorage
     * Called when user successfully logs in
     */
    setCredentials: (state, action) => {
      state.user = action.payload.user;
      state.token = action.payload.token;
      state.userId = action.payload.user.sub;

      // Persist authentication data in localStorage
      localStorage.setItem('token', action.payload.token);
      localStorage.setItem('user', JSON.stringify(action.payload.user));
      localStorage.setItem('userId', action.payload.user.sub);
    },

    /**
     * Clears all authentication data from Redux state and localStorage
     * Called when user logs out
     */
    logout: (state) => {
      state.user = null;
      state.token = null;
      state.userId = null;

      // Remove authentication data from localStorage
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      localStorage.removeItem('userId');
    }
  },
})

// Action creators are generated for each case reducer function
export const { setCredentials, logout } = authSlice.actions;

export default authSlice.reducer;