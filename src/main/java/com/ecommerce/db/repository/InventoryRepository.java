package com.ecommerce.db.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.ecommerce.db.model.Inventory;

public interface InventoryRepository extends MongoRepository<Inventory, String> {

	Optional<Inventory> findByProductName(String productName);

	Optional<Inventory> findByProductIdAndSupplierId(String productId, List<String> supplierIds);

	Optional<Inventory> findByProductId(String productId);

}
