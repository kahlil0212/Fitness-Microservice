import { Box, Button, FormControl, InputLabel, MenuItem, Select, TextField } from "@mui/material";
import { useState } from "react";
import { addActivity } from "../services/api";


const ActivityForm = ({ onActivityAdded }) => {

    const userId = localStorage.getItem('userId');

    const [activity, setActivity] = useState(
        {
            userId,
            activityType: "RUNNING",
            duration: '',
            caloriesBurned: '',
            additionalMetrics: {}
        })
    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await addActivity(activity);
            onActivityAdded();
            setActivity({ userId, activityType: "RUNNING", duration: '', caloriesBurned: '' });
        } catch (error) {
            console.error(error);
        }
    }


    return (
        <Box component="form" sx={{ mb: 4 }} onSubmit={handleSubmit}>
            <FormControl fullWidth sx={{ mb: 2 }}>
                <InputLabel>Activity Type</InputLabel>
                <Select value={activity.activityType} onChange={(e) => setActivity({ ...activity, activityType: e.target.value })}>
                    <MenuItem value="RUNNING">RUNNING</MenuItem>
                    <MenuItem value="WALKING">WALKING</MenuItem>
                    <MenuItem value="CYCLING">CYCLING</MenuItem>
                </Select>
            </FormControl>
            <TextField fullWidth
                label="Duration (Minutes)"
                type="number"
                sx={{ mb: 2 }}
                value={activity.duration}
                onChange={(e) => setActivity({ ...activity, duration: e.target.value })} />
            <TextField fullWidth
                label="Calories Burned"
                type="number"
                sx={{ mb: 2 }}
                value={activity.caloriesBurned}
                onChange={(e) => setActivity({ ...activity, caloriesBurned: e.target.value })} />
            <Button type="submit" variant="contained">Add Activity</Button>
        </Box>
    )
}

export default ActivityForm;