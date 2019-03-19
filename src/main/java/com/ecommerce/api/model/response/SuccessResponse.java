package com.ecommerce.api.model.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SuccessResponse {

	private Boolean success;
	private Date timestamp;
	private String message;
	private Object data;

}
