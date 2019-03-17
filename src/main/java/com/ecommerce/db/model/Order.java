package com.ecommerce.db.model;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.ecommerce.util.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Orders")
public class Order {

	@Id
	private String _id;
	private String orderId;
	private String name;
	private OrderStatus status;
	private LocalDateTime createdOn;
	private LocalDateTime modifiedOn;
	private Double totalAmount;
	private Integer totalQuantity;

	private Account account;

	private List<Inventory> products;

}
