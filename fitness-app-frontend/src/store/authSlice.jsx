import { createSlice } from '@reduxjs/toolkit'

export const authSlice = createSlice({
  name: 'auth',
  initialState: {
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('token') || null,
    userId: localStorage.getItem('userId') || null
  },
  reducers: {
   setCredentials: (state, action) => {

   },
   logout: (state) => {

   }
  },
})

// Action creators are generated for each case reducer function
export const {setCredentials,logout } = authSlice.actions;

export default authSlice.reducer;