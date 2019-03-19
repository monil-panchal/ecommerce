package com.ecommerce.api.controller;

import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.api.model.request.OrderDTO;
import com.ecommerce.api.model.response.SuccessResponse;
import com.ecommerce.db.model.Order;
import com.ecommerce.service.OrderService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/orders")
public class OrderController {

	@Autowired
	private OrderService orderService;

	private SuccessResponse successResponse;

	@Value("${app.response.success.message.order.placed}")
	private String orderPlaced;

	@GetMapping
	public ResponseEntity<?> getOrders() {
		return new ResponseEntity<>(orderService.getAllOrders(), HttpStatus.OK);
	}

	@PostMapping()
	public ResponseEntity<?> createOrder(@Valid @RequestBody OrderDTO orderDTO) throws Exception {
		log.info("Creating the order");

		Order generatedorder = orderService.createOrder(orderDTO);
		successResponse = new SuccessResponse(true, new Date(), orderPlaced, generatedorder);

		return new ResponseEntity<>(successResponse, HttpStatus.CREATED);
	}

}
