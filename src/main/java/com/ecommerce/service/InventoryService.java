package com.ecommerce.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ecommerce.api.model.request.InventoryDTO;
import com.ecommerce.db.model.Inventory;
import com.ecommerce.db.model.Inventory.Supplier;
import com.ecommerce.db.model.Order;
import com.ecommerce.db.query.InventoryQuery;
import com.ecommerce.db.repository.InventoryRepository;
import com.ecommerce.rabbitmq.RabbitMqOrderConsumer;

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

	@Autowired
	private RabbitMqOrderConsumer rabbitMqOrderConsumer;

	public Inventory addToInventory(InventoryDTO inventoryDTO) {
		log.info("Add new item in the inventory");
		Inventory inventory = (Inventory) ModelMapperUtil.map(inventoryDTO, Inventory.class);

		inventory.setProductId(idGenerator.randomString(IdEnum.PR.toString()));

		return inventoryRepository.save(inventory);

	}

	// @Async("threadPoolTaskExecutor")
	// public CompletableFuture<Boolean> updateInventoryQuantity(List<Inventory>
	// inventory) {
	//
	// log.info("Inventory to be updated: " + inventory);
	// inventory.stream().forEach(p -> {
	// UpdateResult i =
	// inventoryQuery.updateItemQuantityAfterOrder(p.getProductId(),
	// p.getSupplier().get(0).getId(), p.getSupplier().get(0).getQuantity());
	//
	// log.info("Inventory updated: " + i);
	//
	// });
	// return CompletableFuture.completedFuture(true);
	//
	// }

	// This method will listen to RabbitMQ order queue and fetch the Order object.
	// Using the order object, it will update the inventory

	@RabbitListener(queues = "${ecommerce.rabbitmq.queue}")
	public void updateInventoryQuantityForSuccessfulOrder(Order newOrder) {
		log.info("newOrder: " + newOrder);

		List<String> productsPurchased = newOrder.getProducts().stream().map(Inventory::getProductName)
				.collect(Collectors.toList());

		Optional<List<Inventory>> inventoryOptional = getAvailableProducts(productsPurchased);
		List<Inventory> inventoryList = inventoryOptional.get();

		newOrder.getProducts().forEach(orderedProduct -> {
			inventoryList.stream().forEach(inventory -> {
				if (inventory.getProductName().equals(orderedProduct.getProductName())) {
					Supplier s = inventory.getSupplier().stream()
							.filter(ss -> ss.getId().equals(orderedProduct.getSupplier().get(0).getId())).findFirst()
							.get();
					s.setQuantity(s.getQuantity() - orderedProduct.getSupplier().get(0).getQuantity());
				}
			});
		});

		log.info("Modified inventory list after updating inventory quantity:" + inventoryList);

		inventoryRepository.saveAll(inventoryList);

		// return CompletableFuture.completedFuture(true);

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
