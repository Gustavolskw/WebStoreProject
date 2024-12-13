package com.ms.microservices.order;

import com.ms.microservices.order.stubs.InventoryCleintStubs;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MySQLContainer;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 8098)
class OrderServiceApplicationTests {
	@ServiceConnection
	static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.3.0");


	@LocalServerPort
	private Integer port = 8099;

	@BeforeEach
	void setUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port= port;
	}

	static {
		mySQLContainer.start();
	}



	@Test
	void placeOrderTest() {

		String requestBody = """
				{
				    "skuCode":"IPhone_15",
				    "price":35.84,
				    "quantity":100
				}
				""";

		InventoryCleintStubs.stubInventoryCall("IPhone_15", 100);

		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("api/order")
				.then()
				.statusCode(201)
				.body("data", Matchers.notNullValue())
				.body("data.id", Matchers.notNullValue())
				.body("data.orderNumber", Matchers.notNullValue())
				.body("data.skuCode", Matchers.equalTo("IPhone_15"))
				.body("data.price", Matchers.equalTo(35.84F))
				.body("data.quantity", Matchers.equalTo(100));
	}

	@Test
	void placeOrderFalseTest() {

		String requestBody = """
				{
				    "skuCode":"IPhone_15",
				    "price":35.84,
				    "quantity":1000
				}
				""";

		InventoryCleintStubs.stubInventoryCall("IPhone_15", 1000);

		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("api/order")
				.then()
				.statusCode(400)
				.body("data", Matchers.nullValue());
	}

}
