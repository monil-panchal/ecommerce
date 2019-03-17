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
import com.ecommerce.service.InventoryService;
import com.ecommerce.util.IdGenerator;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/inventory")
public class InventoryContoller {

	@Autowired
	private InventoryService inventoryService;

	@PostMapping
	public ResponseEntity<?> createOrder(@Valid @RequestBody InventoryDTO inventoryDTO) {
		return new ResponseEntity<>(inventoryService.addToInventory(inventoryDTO), HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<?> getInventory() {
		return new ResponseEntity<>(inventoryService.getInventory(), HttpStatus.OK);
	}
}
