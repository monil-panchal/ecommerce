# Ecommerce spring boot application.
Contains order and inventory functionality

## Tech stack
    1. Oracle JDK 1.8+
    2. Maven 3.3.9
    3. RabbitMQ
    4. MongoDB
    

## Build and and Run application
    1. Build project: mvn clean install
    2. Run spring boot service: mvn spring-boot:run
    3. MongoDB and RabbitMQ are configured on the cloud. No need to set it up in local

## Endpoints

# Ecommerce Base URI
http://localhost:9090/ecommerce/

# Health check
http://localhost:9090/ecommerce/actuator/health

# Swagger UI
http://localhost:9090/ecommerce/swagger-ui.html


# How to place an order

1. Hit the inventory endpoint to get product details: GET http://localhost:9090/ecommerce/inventory
2. Copy the name of the product(s)(field: productName) you want to order.
	Sample: 
	```
	{
	"user":{
		"name":"monil",
		"phoneNumber": 9998788448,
		"email": "monil@gmail.com",
		"address": {
			"addressLine1": "185"
		}
	},
	"product": [

	{
		"name": "Apple iPhone 8 (64GB) - Gold",
		"quantity": 2
	},
		{
		"name": "OnePlus 6T (Midnight Black, 8GB RAM, 256GB Storage)",
		"quantity": 2
	}
		]
}
	```
3. Construct order request payload. Sample request can be found at: http://localhost:9090/ecommerce/swagger-ui.html#/order-controller/createOrderUsingPOST

One or more product can be passed in "product" list. 

# Succinct overview of create order workflow

Create order is composed of 3 internal workflows
1. Querying the DB for product availability.

	This returns the details of products which the user wants to order.
2. Validate and filter product based on availability.

	Based on the list of available products, this validates and filter out the product based on the current quantity 	available with the supplier(Seller).
	This step will handle most of the validations and scenarions(product not available, out of stock etc).
	
	#how a particular supplier(seller is selected)?
	There is a custom logic implemented(this surely depends on the business requirement) where the seller with lowest price 	for the product and highest available quantity is picked for the order generation(Since order request doesn't explicitly 	specify which seller to choose and at which price)

	#Reason why this is implemented is to ensure the user gets the product at best available price can order multiple 		quantities if it's available with the seller.
	
3. Order generation 
	More of a Order object generation step which aggregates details from the request payload and calculates total quantity and total amount for the order.
	
# Race condition 	
Once the order is generated, it is pushed to RabbitMq queue.
The consumer for this message(order generated is the updateInventoryQuantityForSuccessfulOrder() method of Inventory service which ensures the quantity after the order is generated is accurately modified.

After the inventory is updated, the order is saved in the DB
(This ensures even if there are multiple orders for the same product at the same time, the asynchronous event driven function will handle the race condition for the quantities)


Since the inventory is first updated based on the newly generated order and then it's saved, this will handle the race condition. Asynchronous event driven approach will ensure the inventory is updated as soon as the order is generated.

*** alternate way of handling race condition - Java concurrency approach ***

Ideal way to handle this would be using ReadWriteLock on the inventory method which is updating the inventory object.
Another way is to make the Inventory object Immutable
	


# Please note

Due to time crunch, the following functionalities couldn't be implemented:
1.  Users/account binding. The current application is user agonistic
2. Junits. Since there is an integration with Rabbit MQ, Intergration test of create Order couldn't be done due to rabbit mq test dependencies





