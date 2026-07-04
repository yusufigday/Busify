package com.yusufgun.busify.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_TICKET = "busify.ticket.queue";
    public static final String QUEUE_USER = "busify.user.queue";
    public static final String QUEUE_AUTH = "busify.auth.queue";

    public static final String EXCHANGE = "busify.exchange";

    public static final String ROUTING_TICKET = "busify.ticket.routing";
    public static final String ROUTING_USER = "busify.user.routing";
    public static final String ROUTING_AUTH = "busify.auth.routing";


    @Bean
    public Queue ticketQueue() {
        return new Queue(QUEUE_TICKET, true);
    }

    @Bean
    public Queue userQueue() {
        return new Queue(QUEUE_USER, true);
    }

    @Bean
    public Queue authQueue() {
        return new Queue(QUEUE_AUTH, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding ticketBinding(Queue ticketQueue, TopicExchange exchange) {
        return BindingBuilder.bind(ticketQueue).to(exchange).with(ROUTING_TICKET);
    }

    @Bean
    public Binding userBinding(Queue userQueue, TopicExchange exchange) {
        return BindingBuilder.bind(userQueue).to(exchange).with(ROUTING_USER);
    }

    @Bean
    public Binding authBinding(Queue authQueue, TopicExchange exchange) {
        return BindingBuilder.bind(authQueue).to(exchange).with(ROUTING_AUTH);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}