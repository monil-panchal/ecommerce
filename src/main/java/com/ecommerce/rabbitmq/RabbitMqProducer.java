package com.ecommerce.rabbitmq;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ecommerce.db.model.Order;

@Component
public class RabbitMqProducer {

	@Autowired
	private AmqpTemplate amqpTemplate;

	@Value("${ecommerce.rabbitmq.exchange}")
	private String exchange;

	@Value("${ecommerce.rabbitmq.routingkey}")
	private String routingKey;

	public void produceMsg(Order message) {
		amqpTemplate.convertAndSend(exchange, routingKey, message);
		System.out.println("Send msg = " + message);
	}

}
