package com.ecommerce.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.api.model.request.InventoryDTO;
import com.ecommerce.db.model.Inventory;
import com.ecommerce.db.model.Inventory.Supplier;
import com.ecommerce.db.model.Order;
import com.ecommerce.db.query.InventoryQuery;
import com.ecommerce.db.repository.InventoryRepository;
import com.ecommerce.util.IdEnum;
import com.ecommerce.util.IdGenerator;
import com.ecommerce.util.ModelMapperUtil;

import lombok.extern.slf4j.Slf4j;

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
