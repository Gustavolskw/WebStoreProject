package com.ms.microservices;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MongoDBContainer;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTests {

	@ServiceConnection
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.5");

	@LocalServerPort
	private Integer port = 8099;

	@BeforeEach
	void setUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port=this.port;
	}

	static {
		mongoDBContainer.start();
	}

	@Test
	void shouldCreatProduct() {
		String requestBody = """
				{
				    "name":"IPhone 15",
				    "description":"Big Papa Cell phone",
				    "price":750.45
				}
				""";

		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("api/product")
				.then()
				.statusCode(201)
				.body("data", Matchers.notNullValue())
				.body("data.id", Matchers.notNullValue())
				.body("data.name", Matchers.equalTo("IPhone 15"))
				.body("data.description", Matchers.equalTo("Big Papa Cell phone"))
				.body("data.price", Matchers.equalTo(750.45F));
	}


	@Test
	void shouldGetAllProducts() {
		RestAssured.given()
				.contentType("application/json")
				.when()
				.get("/api/product")
				.then()
				.statusCode(200)
				.body("data", Matchers.notNullValue())
				.body("data.size()", Matchers.greaterThan(0))  // Assert the list is not empty
				.body("data[0].name", Matchers.notNullValue())  // Assert the name of the first product
				.body("data[0].price", Matchers.greaterThan(0F));  // Assert the price of the first product
	}

}
