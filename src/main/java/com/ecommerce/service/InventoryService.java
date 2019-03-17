package com.ecommerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.api.model.request.InventoryDTO;
import com.ecommerce.db.model.Inventory;
import com.ecommerce.db.query.InventoryAvailiblityQuery;
import com.ecommerce.db.repository.InventoryRepository;

import lombok.extern.slf4j.Slf4j;

import com.ecommerce.util.IdEnum;
import com.ecommerce.util.IdGenerator;
import com.ecommerce.util.ModelMapperUtil;
import com.jayway.jsonpath.Option;

@Service
@Slf4j
public class InventoryService {

	@Autowired
	private InventoryRepository inventoryRepository;
	@Autowired
	IdGenerator idGenerator;

	@Autowired
	private InventoryAvailiblityQuery inventoryAvailiblityQuery;

	public Inventory addToInventory(InventoryDTO inventoryDTO) {
		log.info("Add new item in the inventory");
		Inventory inventory = (Inventory) ModelMapperUtil.map(inventoryDTO, Inventory.class);

		inventory.setProductId(idGenerator.randomString(IdEnum.PR.toString()));

		return inventoryRepository.save(inventory);

	}

	public List<Inventory> getInventory() {
		return inventoryRepository.findAll();
	}

	public Optional<Inventory> getProduct(String productName) {
		return inventoryRepository.findByProductName(productName);
	}

	public Optional<List<Inventory>> getAvailableProducts(List<String> products) {
		return Optional.of(inventoryAvailiblityQuery.findAvailableProducts(products));
	}

}
