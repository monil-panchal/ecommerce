package com.ecommerce.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.mockito.exceptions.verification.NeverWantedButInvoked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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

		List<String> productsToOrder = orderDTO.getProduct().stream().map(Product::getName)
				.collect(Collectors.toList());

		// Query the DB for product availability
		Optional<List<Inventory>> products = inventoryService.getAvailableProducts(productsToOrder);

		if (products.isPresent()) {
			List<Inventory> availableProducts = products.get();

			// Items added to order but not existing in inventory

			// validate and filter product object
			Map<Inventory, Supplier> map = validateAndFilterProductAvailability(availableProducts, orderDTO);

			// Call builder method to construct Order object
			CompletableFuture<Order> newOrder = buildOrder(map, orderDTO);

			// Call UpdateInventoryMethod
			List<Inventory> updateInventory = new ArrayList<>();

			Map<String, Integer> map2 = orderDTO.getProduct().stream()
					.collect(Collectors.toMap(Product::getName, Product::getQuantity));

			map.forEach((producer, supplier) -> {

				Inventory i = new Inventory();
				i.setProductId(producer.getProductId());

				List<Supplier> supplierList = new ArrayList<>();
				Supplier s = new Supplier();
				s.setId(supplier.getId());
				s.setQuantity(map.get(producer).getQuantity() - map2.get(producer.getProductName()));
				supplierList.add(s);

				i.setSupplier(supplierList);

				updateInventory.add(i);

			});

			CompletableFuture<Boolean> updateInventoryFuture = inventoryService
					.updateInventoryQuantity(updateInventory);

			newOrder.get();
			updateInventoryFuture.get();

			orderRepository.save(newOrder.get());

		}

		return null;

	}

	private Map<Inventory, Supplier> validateAndFilterProductAvailability(List<Inventory> products, OrderDTO orderDTO)
			throws Exception {

		log.info("products: " + products);

		Map<String, Integer> productQuantityMap = orderDTO.getProduct().stream()
				.collect(Collectors.toMap(Product::getName, Product::getQuantity));

		Map<String, Integer> unavailabeProductMap = new HashMap<>();

		List<String> productsToOrder = productQuantityMap.keySet().stream().collect(Collectors.toList());

		if (products.isEmpty()) {

			throw new Exception(
					"Following items do not exists in Inventory. Please try providing correct product name. "
							+ products);
		}

		else if (products.size() < orderDTO.getProduct().size()) {

			List<String> productNameList = products.stream().map(product -> product.getProductName())
					.collect(Collectors.toList());

			orderDTO.getProduct().removeIf(product -> !productNameList.contains(product.getName()));

			productsToOrder = orderDTO.getProduct().stream().map(Product::getName).collect(Collectors.toList());

			throw new Exception(
					"Following items do not exists in Inventory. Please try providing correct product name. "
							+ productsToOrder);
		}

		Map<Inventory, Supplier> supplierMap = new HashMap<>();

		products.parallelStream().forEach(product -> {

			Integer quantityRequired = productQuantityMap.get(product.getProductName());

			// Check total quantity
			if (product.getTotalQuantity() >= quantityRequired) {

				// Sort the list of supplier based on lowest price and more number of quantities
				product.getSupplier().sort(inventorySortComparator.sortByPriceAndQuantity());

				// Fetching the appropriate supplier
				product.getSupplier().parallelStream().forEach(supplier -> {
					if (supplier.getQuantity() >= quantityRequired) {
						product.setSupplier(null);

						supplierMap.put(product, supplier);
					}
				});
			}

			else {
				unavailabeProductMap.put(product.getProductName(), product.getTotalQuantity());

			}

		});

		// construct error response
		if (!unavailabeProductMap.isEmpty()) {

			throw new Exception("Following products are currently not available in stock: "
					+ unavailabeProductMap.keySet().stream().collect(Collectors.toList())
					+ "Consider ordering lesser quantity or we'll inform once the product is back in stock. \n"
					+ "Following items are available in stock. Try ordering them seperately."
					+ supplierMap.keySet().stream().map(Inventory::getProductName).collect(Collectors.toList()));
		}

		log.info("supplierMap: " + supplierMap);

		return supplierMap;

	}

	// private void createInventoryOrder(Inventory productItem) {
	// productItem.get
	//
	// }
	//
	@Async("threadPoolTaskExecutor")
	private CompletableFuture<Order> buildOrder(Map<Inventory, Supplier> availableProducts, OrderDTO orderDTO) {

		Map<String, Integer> productQuantityMap = orderDTO.getProduct().stream()
				.collect(Collectors.toMap(Product::getName, Product::getQuantity));

		Double totalAmount[] = { 0.0 };
		Integer totalQuantity[] = { 0 };
		StringBuilder orderName = new StringBuilder();

		Order newOrder = new Order();
		newOrder.setOrderId(idGenerator.randomString(IdEnum.OR.toString()));
		newOrder.setStatus(OrderStatus.PLACED);
		newOrder.setCreatedOn(LocalDateTime.now());

		List<Inventory> productOrdered = new ArrayList<>();

		// Calculate total amount from the supplier- inventory
		availableProducts.forEach((product, supplier) -> {

			Integer productQuantity = productQuantityMap.get(product.getProductName());

			totalAmount[0] = totalAmount[0] + (supplier.getPrice() * productQuantity);

			orderName.append(product.getProductName() + " and ");

			Inventory i = product;

			List<Supplier> s = new ArrayList<>();
			s.add(supplier);
			i.setSupplier(s);
			productOrdered.add(i);
		});
		newOrder.setName(orderName.toString());

		newOrder.setProducts(productOrdered);

		// calculate total order quantity
		orderDTO.getProduct().stream().forEach(product -> {
			totalQuantity[0] = totalQuantity[0] + product.getQuantity();
		});

		newOrder.setTotalAmount(totalAmount[0]);
		newOrder.setTotalQuantity(totalQuantity[0]);

		log.info("Order object to be saved: " + newOrder);

		return CompletableFuture.completedFuture(newOrder);

	}

	public List<Order> getAllOrders() {
		return orderRepository.findAll();
	}

}
