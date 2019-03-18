package com.ecommerce.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

@Configuration
public class RabbitMqConfig implements RabbitListenerConfigurer {

	// @Value("${ecommerce.rabbitmq.exchange}")
	// private String exchange;
	//
	// @Value("${ecommerce.rabbitmq.routingkey}")
	// private String routingKey;
	//
	// @Value("${ecommerce.rabbitmq.queue}")
	// private String queue;
	//
	// @Bean
	// Queue ordersQueue() {
	// return QueueBuilder.durable(queue).build();
	// }
	//
	// @Bean
	// Exchange ordersExchange() {
	// return ExchangeBuilder.topicExchange(exchange).build();
	// }
	//
	// @Bean
	// Binding binding(Queue ordersQueue, TopicExchange ordersExchange) {
	// return BindingBuilder.bind(ordersQueue).to(ordersExchange).with(queue);
	// }
	//
	// @Bean
	// public RabbitTemplate rabbitTemplate(final ConnectionFactory
	// connectionFactory) {
	// final RabbitTemplate rabbitTemplate = new RabbitTemplate();
	// rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
	// return rabbitTemplate;
	// }
	//
	// @Bean
	// public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
	// return new Jackson2JsonMessageConverter();
	// }
	//
	// @Override
	// public void configureRabbitListeners(RabbitListenerEndpointRegistrar arg0) {
	// // TODO Auto-generated method stub
	//
	// }

	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
		return rabbitTemplate;
	}

	@Bean
	public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Override
	public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
		registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());

	}

	@Bean
	MessageHandlerMethodFactory messageHandlerMethodFactory() {
		DefaultMessageHandlerMethodFactory messageHandlerMethodFactory = new DefaultMessageHandlerMethodFactory();
		messageHandlerMethodFactory.setMessageConverter(consumerJackson2MessageConverter());
		return messageHandlerMethodFactory;
	}

	@Bean
	public MappingJackson2MessageConverter consumerJackson2MessageConverter() {
		return new MappingJackson2MessageConverter();
	}

}
