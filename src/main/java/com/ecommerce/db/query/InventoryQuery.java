package com.ecommerce.db.query;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import com.ecommerce.db.model.Inventory;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InventoryQuery {

	@Autowired
	private MongoOperations mongoOperations;

	// @Autowired
	// private MongoTemplate mongoTemplate;

	public static final String COLLECTION_NAME = "Inventory";

	public List<Inventory> findAvailableProducts(List<String> products) {
		log.info("Executing inventory query");

		Aggregation aggregation = Aggregation.newAggregation(Aggregation.unwind("$supplier"),
				project("_id", "productName", "supplier", "productId", "description", "price", "tags", "category"),
				match(where("productName").in(products)),
				group("_id", "productName", "productId", "description", "price", "tags", "category").push("supplier")
						.as("supplier")

		);

		AggregationResults<Inventory> results = mongoOperations.aggregate(aggregation, COLLECTION_NAME,
				Inventory.class);
		List<Inventory> inventoryList = results.getMappedResults();
		return inventoryList;

	}

//	public UpdateResult updateItemQuantityAfterOrder(String productId, String supplierId, Integer quantity) {
//		Query select = Query.query(Criteria.where("productId").is("productId"));
//
//		select.addCriteria(Criteria.where("supplier.id").is(supplierId));
//		Update update = new Update().set("supplier.$.quantity", quantity);
//
//		// return mongoTemplate.findAndModify(select, update, Inventory.class);
//
//		UpdateResult wr = mongoTemplate.updateMulti(
//				new Query(where("productId").is(productId)).addCriteria(where("supplier.id").is(supplierId)),
//				new Update().set("supplier.$.quantity", quantity), Inventory.class);
//		return wr;
//
//	}

}
