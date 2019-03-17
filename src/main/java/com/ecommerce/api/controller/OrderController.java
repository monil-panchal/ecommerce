package com.ecommerce.api.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.api.model.request.InventoryDTO;
import com.ecommerce.api.model.request.OrderDTO;
import com.ecommerce.service.OrderService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/orders")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@PostMapping()
	public ResponseEntity<?> createOrder(@Valid @RequestBody OrderDTO orderDTO) throws Exception {
		log.info("Creating the order");

		return new ResponseEntity<>(orderService.createOrder(orderDTO), HttpStatus.OK);
	}

}
