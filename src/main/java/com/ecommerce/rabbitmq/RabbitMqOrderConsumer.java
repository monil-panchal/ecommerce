package com.ecommerce.rabbitmq;

import org.springframework.stereotype.Component;

import com.ecommerce.db.model.Order;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

@Component
public class RabbitMqOrderConsumer {
//	@RabbitListener(queues = "${ecommerce.rabbitmq.queue}")
//	public Order recievedMessage(Order msg) {
//
//		System.out.println("Recieved Message: " + msg);
//		return msg;
//	}

}
