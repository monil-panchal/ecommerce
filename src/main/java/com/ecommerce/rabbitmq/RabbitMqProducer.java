package com.ecommerce.rabbitmq;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqProducer {

	@Autowired
	private AmqpTemplate amqpTemplate;

	@Value("${ecommerce.rabbitmq.exchange}")
	private String exchange;

	@Value("${ecommerce.rabbitmq.routingkey}")
	private String routingKey;

	public void produceMsg(Object message) {
		amqpTemplate.convertAndSend(exchange, routingKey, message.toString());
		System.out.println("Send msg = " + message);
	}

}
