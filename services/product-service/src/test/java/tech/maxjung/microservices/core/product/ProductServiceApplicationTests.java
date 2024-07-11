package tech.maxjung.microservices.core.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ProductServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Test
	void getProductById() {

		int productId = 1;

		client.get()
			.uri("/product/" + productId)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.productId").isEqualTo(productId);
	}

	@Test
	void getProductInvalidParameterString() {

		client.get()
			.uri("/product/no-integer")
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(BAD_REQUEST)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.path").isEqualTo("/product/no-integer")

			/* !! ! relies on configuration: server.error.include-message=always !!!
				 see:
				 https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.3-Release-Notes#changes-to-the-default-error-pages-content
				 https://www.baeldung.com/exception-handling-for-rest-with-spring#spring-boot
				 https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html#server.error.include-message
			 */
			.jsonPath("$.message").isEqualTo("Type mismatch.");

	}

	@Test
	void getProductNotFound() {

		int productIdNotFound = 13;

		client.get()
			.uri("/product/" + productIdNotFound)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isNotFound()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
				.jsonPath("$.path").isEqualTo("/product/" + productIdNotFound)
				.jsonPath("$.message").isEqualTo("No product found for productId: " + productIdNotFound);
	}

	@Test
	void getProductInvalidParameterNegativeValue() {

		int productIdInvalid = -1;

		client.get()
			.uri("/product/" + productIdInvalid)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
				.jsonPath("$.path").isEqualTo("/product/" + productIdInvalid)
				.jsonPath("$.message").isEqualTo("Invalid productId: " + productIdInvalid);
	}
}
