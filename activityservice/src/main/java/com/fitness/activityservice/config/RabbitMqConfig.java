package com.fitness.activityservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMqConfig {

    //RabitMQ properties
    @Value("${rabbitmq.queue.name}")
    private String queue;
    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    //Declares a queue named activityQueue
    @Bean
    public Queue activityQueue(){
        return new Queue(queue,true); //true allows for queue to remain after restart
    }

    //These beans are required to bind the queue to the exchange to be able to pick up messages
    // that are sent by the service (activityExchange and activityBinding)
    @Bean
    public DirectExchange activityExchange(){
        return new DirectExchange(exchange);
    }
    @Bean
    public Binding activityBinding(Queue activityQueue, DirectExchange activityExchange){
        return BindingBuilder.bind(activityQueue).to(activityExchange).with(routingKey);
    }


    //Converts java objects to json when sending messages to queue
    //Makes easier so you do not have to manually serialize objects
    @Bean
    public MessageConverter jsonMessageConverter(){
        return new JacksonJsonMessageConverter();
    }
}
