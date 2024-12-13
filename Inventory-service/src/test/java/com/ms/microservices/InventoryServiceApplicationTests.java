package com.ms.microservices;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MySQLContainer;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryServiceApplicationTests {

	@ServiceConnection
	static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.3.0");


	@LocalServerPort
	private Integer port = 8099;

	@BeforeEach
	void setUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port=this.port;
	}

	static {
		mySQLContainer.start();
	}



	@Test
	void GetExistentProductTest() {
		mySQLContainer.withInitScript("INSERT INTO t_inventory (product_name, quantity) value('IPhone_15', 100);");


		RestAssured.given()
				.contentType("application/json")
				.when()
				.get("api/inventory?ProductName=IPhone_15&Quantity=100")
				.then()
				.statusCode(200)
				.body("data", Matchers.notNullValue())
				.body("data", Matchers.equalTo(true));
	}

	@Test
	void GetInexistentProductTest() {


		RestAssured.given()
				.contentType("application/json")
				.when()
				.get("api/inventory?ProductName=iphone&Quantity=1002323")
				.then()
				.statusCode(200)
				.body("data", Matchers.notNullValue())
				.body("data", Matchers.equalTo(false));
	}

}
