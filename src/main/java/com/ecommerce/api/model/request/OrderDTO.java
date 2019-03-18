package com.ecommerce.api.model.request;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import com.ecommerce.db.model.Account.User.Address;

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
public class OrderDTO {

	@NotNull
	private User user;

	@NotNull
	private List<Product> product;

	@Setter
	@Getter
	@ToString
	@AllArgsConstructor
	@NoArgsConstructor
	public static class User {

		@NotBlank
		private String name;
		@NotBlank
		private String phoneNumber;
		@NotBlank
		private String email;
		@NotNull
		private Address address;

	}

	@Setter
	@Getter
	@ToString
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Product {
		@NotBlank
		private String name;
		@Min(value = 1, message = "Minimum quantity should be 1")
		private Integer quantity;

	}

}
