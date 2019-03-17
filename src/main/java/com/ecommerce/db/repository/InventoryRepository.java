package com.ecommerce.db.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.ecommerce.db.model.Inventory;

public interface InventoryRepository extends MongoRepository<Inventory, String> {

	Optional<Inventory> findByProductName(String productName);

}
