package com.ecommerce.db.query;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import com.ecommerce.db.model.Inventory;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InventoryAvailiblityQuery {

	@Autowired
	MongoOperations mongoOperations;

	public static final String COLLECTION_NAME = "Inventory";

	public List<Inventory> findAvailableProducts(List<String> products) {
		log.info("Executing inventory query");

		Aggregation aggregation = Aggregation.newAggregation(Aggregation.unwind("$supplier"),
				project("_id", "productName", "supplier", "productId", "description", "price", "tags", "category"),
				match(where("productName").in(products)), match(where("supplier.quantity").gte(1)),
				group("_id", "productName", "productId", "description", "price", "tags", "category").push("supplier")
						.as("supplier")

		);

		AggregationResults<Inventory> results = mongoOperations.aggregate(aggregation, COLLECTION_NAME,
				Inventory.class);
		List<Inventory> inventoryList = results.getMappedResults();
		log.info("Inventory query result: " + inventoryList);
		return inventoryList;

	}

}
