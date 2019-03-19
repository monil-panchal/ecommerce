//package com.ecommerce.test.api;
//
//import static org.hamcrest.CoreMatchers.any;
//import static org.junit.Assert.assertEquals;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import java.io.IOException;
//import java.util.Date;
//
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mockito;
//import org.springframework.amqp.rabbit.annotation.Queue;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.TestComponent;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.RequestBuilder;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import com.ecommerce.api.controller.OrderController;
//import com.ecommerce.api.model.request.OrderDTO;
//import com.ecommerce.api.model.response.SuccessResponse;
//import com.ecommerce.db.model.Inventory;
//import com.ecommerce.db.model.Order;
//import com.ecommerce.db.repository.InventoryRepository;
//import com.ecommerce.db.repository.OrderRepository;
//import com.ecommerce.service.OrderService;
//import com.fasterxml.jackson.core.JsonParseException;
//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.rabbitmq.client.ConnectionFactory;
//
//@RunWith(SpringRunner.class)
//@WebMvcTest(value = OrderController.class)
//
//public class OrderControllerTest {
//
//	@Autowired
//	private MockMvc mockMvc;
//
//	@MockBean
//	private OrderController orderController;
//
//	@MockBean
//	private OrderService orderService;
//
//	@MockBean
//	private OrderRepository orderRepository;
//
//	@MockBean
//	private InventoryRepository inventoryRepository;
//
//	@Autowired
//	private RabbitTemplate rabbitTemplate;
//
//	// @Autowired
//	// private Queue queue1;
//	//
//	// @Autowired
//	// private RabbitListenerTestHarness harness;
//
//	@Test
//	public void createOrder() throws Exception {
//
//		String newOrderString = "{\n" + "	\"user\":{\n" + "		\"name\":\"monil\",\n"
//				+ "		\"phoneNumber\": 9998788448,\n" + "		\"email\": \"monil@gmail.com\",\n"
//				+ "		\"address\": {\n" + "			\"addressLine1\": \"185\"\n" + "		}\n" + "	},\n"
//				+ "	\"product\": [\n" + "\n" + "	{\n" + "		\"name\": \"Apple iPhone 8 (64GB) - Gold\",\n"
//				+ "		\"quantity\": 1\n" + "	}\n" + "		]\n" + "}";
//
//		ObjectMapper mapper = new ObjectMapper();
//		OrderDTO newOrder = mapper.readValue(newOrderString, OrderDTO.class);
//
//		String newOrderResponse = "{\n" + "        \"_id\": \"5c90b594be06c54a6787bb05\",\n"
//				+ "        \"orderId\": \"OR-eDKBj220VNu1\",\n"
//				+ "        \"name\": \"Apple iPhone 8 (64GB) - Gold and \",\n" + "        \"status\": \"PLACED\",\n"
//				+ "        \"createdOn\": \"2019-03-19T09:25:40.482+0000\",\n" + "        \"totalAmount\": 55990,\n"
//				+ "        \"totalQuantity\": 1,\n" + "        \"account\": {\n" + "            \"user\": {\n"
//				+ "                \"name\": \"monil\",\n" + "                \"phoneNumber\": \"9998788448\",\n"
//				+ "                \"email\": \"monil@gmail.com\",\n" + "                \"address\": [\n"
//				+ "                    {\n" + "                        \"addressLine1\": \"185\"\n"
//				+ "                    }\n" + "                ]\n" + "            }\n" + "        },\n"
//				+ "        \"products\": [\n" + "            {\n"
//				+ "                \"_id\": \"5c8dcea4be06c531cdff287b\",\n"
//				+ "                \"productId\": \"PR-ReISff3rqRYF\",\n"
//				+ "                \"productName\": \"Apple iPhone 8 (64GB) - Gold\",\n"
//				+ "                \"description\": \"Phone 8 features an all-glass design and an aerospace-grade aluminum band. Charges wirelessly. Resists water and dust. 4.7-inch Retina HD display with True Tone. 12MP camera with an advanced image signal processor. Powered by the A11 Bionic chip. Supports augmented reality experiences in games and apps. And iOS 12—the most advanced mobile operating system—with powerful new tools that make iPhone more personal than ever.\",\n"
//				+ "                \"tags\": [\n" + "                    \"Smartphone\",\n"
//				+ "                    \"apple\",\n" + "                    \"iphone\",\n"
//				+ "                    \"iphone 8\"\n" + "                ],\n"
//				+ "                \"category\": \"mobile\",\n" + "                \"supplier\": [\n"
//				+ "                    {\n" + "                        \"id\": \"SP-GS2gSKEUmkrx0b4\",\n"
//				+ "                        \"name\": \"Cloudtail retail\",\n"
//				+ "                        \"contactName\": \"Jeff Bezos\",\n"
//				+ "                        \"description\": \"India's largest supplier of electronic products\",\n"
//				+ "                        \"phoneNumber\": 911234567891,\n"
//				+ "                        \"price\": 55990,\n" + "                        \"quantity\": 1\n"
//				+ "                    }\n" + "                ]\n" + "            }\n" + "        ]\n" + "    }";
//		 Order generatedOrder = mapper.readValue(newOrderResponse, Order.class);
//
//		mockMvc.perform(post("/ecommerce/orders").contentType(MediaType.APPLICATION_JSON).content(newOrderResponse))
//				.andExpect(status().isCreated());
//
//
//
//	}
//
//}
