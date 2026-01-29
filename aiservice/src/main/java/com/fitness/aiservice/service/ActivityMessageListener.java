package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.stereotype.Service;


/**
 * Listener class for when messages are sent to the queue so that they can then be processed
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {

    private final ActivityAIService activityAIService;



    @RabbitListener(queues = "activity.queue")
    public void processActivity(Activity activity) {
        log.info("Received Activity for processing: {}", activity.getId());
        log.info("Generated Recommendation: {}", activityAIService.generateRecommendation(activity));
    }
}
