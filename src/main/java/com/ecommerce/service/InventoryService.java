package com.ecommerce.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ecommerce.api.model.request.InventoryDTO;
import com.ecommerce.db.model.Inventory;
import com.ecommerce.db.query.InventoryQuery;
import com.ecommerce.db.repository.InventoryRepository;

import lombok.extern.slf4j.Slf4j;

import com.ecommerce.util.IdEnum;
import com.ecommerce.util.IdGenerator;
import com.ecommerce.util.ModelMapperUtil;
import com.mongodb.client.result.UpdateResult;

@Service
@Slf4j
public class InventoryService {

	@Autowired
	private InventoryRepository inventoryRepository;
	@Autowired
	IdGenerator idGenerator;

	@Autowired
	private InventoryQuery inventoryQuery;

	public Inventory addToInventory(InventoryDTO inventoryDTO) {
		log.info("Add new item in the inventory");
		Inventory inventory = (Inventory) ModelMapperUtil.map(inventoryDTO, Inventory.class);

		inventory.setProductId(idGenerator.randomString(IdEnum.PR.toString()));

		return inventoryRepository.save(inventory);

	}

	@Async("threadPoolTaskExecutor")
	public CompletableFuture<Boolean> updateInventoryQuantity(List<Inventory> inventory) {

		log.info("Inventory to be updated: " + inventory);
		inventory.stream().forEach(p -> {
			UpdateResult i = inventoryQuery.updateItemQuantityAfterOrder(p.getProductId(), p.getSupplier().get(0).getId(),
					p.getSupplier().get(0).getQuantity());

			log.info("Inventory updated: " + i);

		});
		return CompletableFuture.completedFuture(true);

	}

	public List<Inventory> getInventory() {
		return inventoryRepository.findAll();
	}

	public Optional<Inventory> getProduct(String productName) {
		return inventoryRepository.findByProductName(productName);
	}

	public Optional<List<Inventory>> getAvailableProducts(List<String> products) {
		return Optional.of(inventoryQuery.findAvailableProducts(products));
	}

}
