package com.ecommerce.api.model.request;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class InventoryDTO {

	private String productId;
	@NotEmpty
	private String productName;
	@NotEmpty
	private String description;
	@NotNull
	private Double price;

	@NotEmpty
	private String[] tags;
	@NotEmpty
	private String category;

	@NotEmpty
	private List<Supplier> supplier;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	public static class Supplier {

		@NotEmpty
		private String id;
		@NotEmpty
		private String name;
		@NotEmpty
		private String contactName;
		@NotEmpty
		private String description;
		@NotNull
		private Long phoneNumber;
		@NotNull
		private Double price;
		private Double discountedPrice;
		@NotNull
		private Integer quantity;

	}

}
