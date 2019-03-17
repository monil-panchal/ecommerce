package com.ecommerce.db.model;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Account")
public class Account {

	private User user;
	private UserCredential userCredential;
	@DBRef
	private List<Order> orders;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class User {

		private String name;
		private String phoneNumber;
		private String email;
		private List<Address> Address;
		private Boolean isRegisteredUser;

		@Getter
		@Setter
		@NoArgsConstructor
		@AllArgsConstructor
		public static class Address {

			private String addressLine1;
			private String addressLine2;
			private String landMark;
			private String city;
			private String state;
			private String country;
			private Integer pinCode;

		}

	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserCredential {

		private String username;
		private String password;

	}

}