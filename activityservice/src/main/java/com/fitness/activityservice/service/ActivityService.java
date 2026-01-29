package com.fitness.activityservice.service;

import com.fitness.activityservice.repository.ActivityRepository;
import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserValidationService userValidationService;
    private final RabbitTemplate rabbitTemplate;

    //RabitMQ properties
    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Autowired
    private ModelMapper modelMapper;

    public ActivityResponse trackActivity(ActivityRequest activityRequest) {


        boolean isValidUser = userValidationService.validateUser(activityRequest.getUserId());

        if (!isValidUser) {
            throw new RuntimeException("Invalid user: " + activityRequest.getUserId());
        }

        Activity activity = modelMapper.map(activityRequest, Activity.class);


        Activity savedActivity = activityRepository.save(activity);

        //Publish to RabbitMQ for AI processing
        try{
            rabbitTemplate.convertAndSend(exchange, routingKey, savedActivity);
        } catch (Exception e) {
            log.error("Failed to publish activity to RabbitMQ: ",e);
        }


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
