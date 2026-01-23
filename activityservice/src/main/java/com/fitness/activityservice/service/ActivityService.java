package com.fitness.activityservice.service;

import com.fitness.activityservice.ActivityRepository;
import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserValidationService userValidationService;

    @Autowired
    private ModelMapper modelMapper;

    public ActivityResponse trackActivity(ActivityRequest activityRequest) {


        boolean isValidUser = userValidationService.validateUser(activityRequest.getUserId());

        if (!isValidUser) {
            throw new RuntimeException("Invalid user: " + activityRequest.getUserId());
        }

        Activity activity = modelMapper.map(activityRequest, Activity.class);

        System.out.println("Activity before save after transformation: " + activity);

        Activity savedActivity = activityRepository.save(activity);

        System.out.println("Activity after save after transformation: " + savedActivity);

        return modelMapper.map(savedActivity, ActivityResponse.class);
    }

    public List<ActivityResponse> getUserActivities(String userId) {

        List<Activity> userActivities = activityRepository.findByUserId(userId);

        return userActivities.stream()
                .map(activity -> modelMapper.map(activity, ActivityResponse.class))
                .toList();
    }

    public ActivityResponse getActivityById(String activityId) {

        return activityRepository.findById(activityId)
                .map(activity -> modelMapper.map(activity, ActivityResponse.class))
                .orElseThrow(() ->
                        new RuntimeException("Invalid activity.Activity not found with id: " + activityId));
    }
}
