package com.ecommerce.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ecommerce.api.model.request.OrderDTO;
import com.ecommerce.api.model.request.OrderDTO.Product;
import com.ecommerce.api.model.request.OrderDTO.User;
import com.ecommerce.db.model.Account;
import com.ecommerce.db.model.Inventory;
import com.ecommerce.db.model.Inventory.Supplier;
import com.ecommerce.db.model.Order;
import com.ecommerce.db.repository.OrderRepository;
import com.ecommerce.exception.ProductNotAvailableException;
import com.ecommerce.exception.ProductOutOfStockException;
import com.ecommerce.rabbitmq.RabbitMqProducer;
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

	@Autowired
	RabbitMqProducer rabbitMqProducer;

	public Order createOrder(OrderDTO orderDTO) throws Exception {

		Order generatedOrder = null;

		List<String> productsToOrder = orderDTO.getProduct().stream().map(Product::getName)
				.collect(Collectors.toList());

		// Query the DB for product availability
		Optional<List<Inventory>> products = inventoryService.getAvailableProducts(productsToOrder);
		List<Inventory> availableProducts = products.get();

		// validate and filter product based on availability
		Map<Inventory, Supplier> inventoryAvailableMap = validateAndFilterProductAvailability(availableProducts,
				orderDTO);

		// Call builder method to construct Order object
		CompletableFuture<Order> newOrderFuture = buildOrder(inventoryAvailableMap, orderDTO);
		Order newOrder = newOrderFuture.get();

		// Publishing the newly generated Order to RabbitMq queue.

		// The listener for this message is inventory update method in InventoryService
		// which will update the quantity of products accordingly.
		rabbitMqProducer.produceMsg(newOrder);

		// ****** Handling race condition while generating an order and updating the
		// inventory ********

		// Since the inventory is first updated based on the newly generated order and
		// then it's saved, this will handle the race condition. Asynchronous event
		// driven approach will ensure the inventory is updated as soon as the order is
		// generated.

		// *** alternate way of handling race condition - Java concurrency approach

		// Ideal way to handle this would be using ReadWriteLock on the inventory method
		// which is updating the inventory object.

		// Another way is to make the Inventory object Immutable
		generatedOrder = orderRepository.save(newOrder);

		return generatedOrder;

	}

	private Map<Inventory, Supplier> validateAndFilterProductAvailability(List<Inventory> products, OrderDTO orderDTO)
			throws Exception {

		log.info("products: " + products);

		Map<String, Integer> productQuantityMap = orderDTO.getProduct().stream()
				.collect(Collectors.toMap(Product::getName, Product::getQuantity));
		Map<String, Integer> unavailabeProductMap = new HashMap<>();
		Map<Inventory, Supplier> supplierMap = new HashMap<>();
		List<String> productsToOrder = productQuantityMap.keySet().stream().collect(Collectors.toList());

		if (products.size() < orderDTO.getProduct().size()) {

			if (!(products.isEmpty())) {
				List<String> productNameList = products.stream().map(product -> product.getProductName())
						.collect(Collectors.toList());
				orderDTO.getProduct().removeIf(product -> productNameList.contains(product.getName()));
				productsToOrder = orderDTO.getProduct().stream().map(Product::getName).collect(Collectors.toList());
			}

			throw new ProductNotAvailableException(
					"Following items do not exists in Inventory. Please try providing correct product name. "
							+ productsToOrder);
		}

		products.stream().forEach(product -> {
			Integer quantityRequired = productQuantityMap.get(product.getProductName());

			log.info("product total quantity: " + product.getTotalQuantity());
			log.info("quantityRequired: " + quantityRequired);
			// Check total quantity
			if (product.getTotalQuantity() >= quantityRequired) {

				// Sort the list of supplier based on lowest price and more number of
				// quantities.

				// This is a custom logic set. This will ensure the product which is available
				// at a lower price and is sufficiently available will be picked for the order.

				product.getSupplier().sort(inventorySortComparator.sortByPriceAndQuantity());

				// Fetching the appropriate supplier
				product.getSupplier().parallelStream().forEach(supplier -> {
					if (supplier.getQuantity() >= quantityRequired) {
						product.setSupplier(null);
						supplierMap.put(product, supplier);
					}
				});
			}

			else
				unavailabeProductMap.put(product.getProductName(), product.getTotalQuantity());
		});

		// construct error response
		if (!unavailabeProductMap.isEmpty()) {

			throw new ProductOutOfStockException(
					"Following products:" + unavailabeProductMap.keySet().stream().collect(Collectors.toList())
							+ " are currently not available in stock. "
							+ "Consider ordering lesser quantity or we'll inform once the product is back in stock."
							+ "Following items: "
							+ supplierMap.keySet().stream().map(Inventory::getProductName).collect(Collectors.toList())
							+ " are available in stock. Try ordering them seperately.");
		}
		log.info("supplierMap: " + supplierMap);

		return supplierMap;

	}

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
		newOrder.setCreatedOn(new Date());

		List<Inventory> productOrdered = new ArrayList<>();

		// Calculate total amount from the supplier- inventory
		availableProducts.forEach((product, supplier) -> {

			Integer productQuantity = productQuantityMap.get(product.getProductName());

			totalAmount[0] = totalAmount[0] + (supplier.getPrice() * productQuantity);

			orderName.append(product.getProductName() + " and ");

			Inventory i = product;

			List<Supplier> s = new ArrayList<>();
			supplier.setQuantity(productQuantityMap.get(product.getProductName()));
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
		User user = orderDTO.getUser();

		Account.User userAccount = (com.ecommerce.db.model.Account.User) ModelMapperUtil.map(user, Account.User.class);
		userAccount.setAddress(Arrays.asList(orderDTO.getUser().getAddress()));
		Account account = new Account();

		account.setUser(userAccount);

		newOrder.setAccount(account);

		log.info("Order object to be saved: " + newOrder);

		return CompletableFuture.completedFuture(newOrder);

	}

	public List<Order> getAllOrders() {
		return orderRepository.findAll();
	}

}
