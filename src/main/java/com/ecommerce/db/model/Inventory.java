package com.ecommerce.db.model;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "Inventory")
public class Inventory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private String _id;
	private String productId;
	private String productName;
	private String description;
	private String[] tags;
	private String category;
	private List<Supplier> supplier;

	@Transient
	@JsonIgnore
	public Integer getTotalQuantity() {
		return this.getSupplier().stream().filter(supplier -> supplier.getQuantity() > 0)
				.mapToInt(Supplier::getQuantity).sum();

	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	public static class Supplier implements Serializable  {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String id;
		private String name;
		private String contactName;
		private String description;
		private Long phoneNumber;
		private Double price;
		private Integer quantity;

	}

}
