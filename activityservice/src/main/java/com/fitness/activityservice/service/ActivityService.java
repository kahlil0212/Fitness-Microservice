package com.fitness.activityservice.service;

import com.fitness.activityservice.ActivityRepository;
import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;

    @Autowired
    private ModelMapper modelMapper;

    public ActivityResponse trackActivity(ActivityRequest activityRequest) {




        Activity activity = modelMapper.map(activityRequest, Activity.class);


        System.out.println("Activity before save after transformation: " + activity);

        Activity savedActivity = activityRepository.save(activity);

        System.out.println("Activity after save after transformation: " + savedActivity);


        return modelMapper.map(savedActivity, ActivityResponse.class);
    }
}
