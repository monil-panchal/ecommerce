package com.ecommerce.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.api.model.request.InventoryDTO;
import com.ecommerce.api.model.request.OrderDTO;
import com.ecommerce.api.model.request.OrderDTO.Product;
import com.ecommerce.db.model.Inventory;
import com.ecommerce.db.model.Inventory.Supplier;
import com.ecommerce.db.model.Order;
import com.ecommerce.db.repository.InventoryRepository;
import com.ecommerce.db.repository.OrderRepository;
import com.ecommerce.util.IdEnum;
import com.ecommerce.util.IdGenerator;
import com.ecommerce.util.InventorySortComparator;
import com.ecommerce.util.ModelMapperUtil;
import com.ecommerce.util.OrderStatus;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private InventoryService inventoryService;

	@Autowired
	private InventorySortComparator inventorySortComparator;

	@Autowired
	IdGenerator idGenerator;

	public Order createOrder(OrderDTO orderDTO) throws Exception {

		Inventory productItem = null;
		// Order newOrder = (Order) ModelMapperUtil.map(orderDTO, Order.class);

		List<String> productsToOrder = orderDTO.getProduct().stream().map(Product::getName)
				.collect(Collectors.toList());

		// Query the DB for product availability
		Optional<List<Inventory>> products = inventoryService.getAvailableProducts(productsToOrder);

		if (products.isPresent()) {
			List<Inventory> availableProducts = products.get();

			// Items added to order but not existing in inventory
			if (availableProducts.size() < orderDTO.getProduct().size()) {

				throw new Exception();
			}

			else {
				Map<String, Supplier> map = validateAndFilterProductAvailability(availableProducts, orderDTO);
				log.info("map: "+ map);

			}

			// Items exists in Inventory but it's currently unavailable i.e. quantity is < 0
			// availableProducts.parallelStream().filter(product ->
			// product.getTotalQuantity()>0).

			// Check if item is available

			// Method call
			// if (productItem.getTotalQuantity() > 0) {
			//
			// // Sort based on Supplier's lowest price and highest quantity: For
			// availibility
			// productItem.getSupplier().sort(inventorySortComparator.sortByPriceAndQuantity());
			// log.info("productItem" + productItem);
			//
			// // Build Order object
			//
			// }

		}
		return null;

		// else
		// throw new Exception("Product: " + orderDTO.getProduct().getName() + " not
		// found");

		// return orderRepository.save(newOrder);

	}

	private Map<String, Supplier> validateAndFilterProductAvailability(List<Inventory> products, OrderDTO orderDTO) {

		Map<String, Integer> map = orderDTO.getProduct().stream()
				.collect(Collectors.toMap(Product::getName, Product::getQuantity));

		Map<String, Supplier> supplierMap = new HashMap<>();

		// Check -1 total quantity

		products.parallelStream().forEach(product -> {

			Integer quantityRequired = map.get(product.getProductName());

			// Check total quantity
			if (product.getTotalQuantity() >= quantityRequired) {

				// Sort the list of supplier based on lowest price and more number of quantities

				product.getSupplier().sort(inventorySortComparator.sortByPriceAndQuantity());

				product.getSupplier().parallelStream().forEach(supplier -> {

					if (supplier.getQuantity() >= quantityRequired) {
						supplierMap.put(product.getProductName(), supplier);
					}
				});

			}

		});

		return supplierMap;

	}

	// private void createInventoryOrder(Inventory productItem) {
	// productItem.get
	//
	// }
	//
	// private Order buildOrder(OrderDTO orderDTO, Inventory item) {
	//
	// Order newOrder = new Order();
	// newOrder.setOrderId(idGenerator.randomString(IdEnum.OR.toString()));
	// newOrder.setName(orderDTO.getProduct().getName());
	// newOrder.setStatus(OrderStatus.PLACED);
	// newOrder.setCreatedOn(LocalDateTime.now());
	//
	// return null;
	//
	// }

}
