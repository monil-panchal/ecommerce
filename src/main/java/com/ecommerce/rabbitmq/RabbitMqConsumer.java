package com.ecommerce.rabbitmq;

import org.springframework.stereotype.Component;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@Component
public class RabbitMqConsumer {
	@RabbitListener(queues = "${ecommerce.rabbitmq.queue}")
	public void recievedMessage(String msg) {
		System.out.println("Recieved Message: " + msg);
	}

}
