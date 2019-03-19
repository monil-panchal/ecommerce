package com.ecommerce.exception;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.ecommerce.api.model.response.ErrorResponse;

@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler {

	@Value("${app.response.error.message.product.outof.stock}")
	private String productOutOfStock;

	@Value("${app.response.error.message.product.not.available}")
	private String productNotAvailable;

	@ExceptionHandler(ProductNotAvailableException.class)
	public final ResponseEntity<ErrorResponse> handleProductNotAvailablexception(ProductNotAvailableException ex,
			WebRequest request) {
		ErrorResponse errorDetails = new ErrorResponse(false, new Date(), productNotAvailable, ex.getMessage());
		return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ProductOutOfStockException.class)
	public final ResponseEntity<ErrorResponse> handleProductOutOfStockException(ProductOutOfStockException ex,
			WebRequest request) {
		ErrorResponse errorDetails = new ErrorResponse(false, new Date(), productOutOfStock, ex.getMessage());
		return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public final ResponseEntity<ErrorResponse> handleInvalidInputExceptions(MethodArgumentNotValidException ex,
			WebRequest request) {
		ErrorResponse errorDetails = new ErrorResponse(false, new Date(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
		ErrorResponse errorDetails = new ErrorResponse(false, new Date(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
