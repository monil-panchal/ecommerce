package com.ecommerce.db.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ecommerce.db.model.Order;

public interface OrderRepository extends MongoRepository<Order, String> {

}
