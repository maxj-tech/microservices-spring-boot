package tech.maxjung.microservices.core.review;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import tech.maxjung.api.core.review.Review;
import tech.maxjung.microservices.core.review.persistence.ReviewRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ReviewServiceApplicationTests extends MySqlTestBase {

	@Autowired
	private WebTestClient client;

	@Autowired
	private ReviewRepository repository;

	@BeforeEach
	void setupDb() {
		repository.deleteAll();
	}

	@Test
	void createReviewForProduct() {
		int productId = 1;
		int reviewId = 1;

		postAndVerifyReview(productId, reviewId, HttpStatus.OK)
			.jsonPath("$.productId").isEqualTo(productId)
			.jsonPath("$.reviewId").isEqualTo(reviewId);
	}

	@Test
	void getReviewsByProductId() {
		int productId = 1;

		postAndVerifyReview(productId, 1, HttpStatus.OK);
		postAndVerifyReview(productId, 2, HttpStatus.OK);
		postAndVerifyReview(productId, 3, HttpStatus.OK);

		getAndVerifyReviewsByProductId(productId, HttpStatus.OK)
			.jsonPath("$.length()").isEqualTo(3)
			.jsonPath("$[2].productId").isEqualTo(productId)
			.jsonPath("$[2].reviewId").isEqualTo(3);
	}

	@Test
	void duplicateError() {
		int productId = 1;
		int reviewId = 1;

		postAndVerifyReview(productId, reviewId, HttpStatus.OK);
		postAndVerifyReview(productId, reviewId, HttpStatus.UNPROCESSABLE_ENTITY)
			.jsonPath("$.path").isEqualTo("/review")
			.jsonPath("$.message").value(Matchers.startsWithIgnoringCase("duplicate key"));

		assertEquals(1, repository.count());
	}

	@Test
	void deleteReviews() {

		int productId = 1;
		int reviewId = 1;

		postAndVerifyReview(productId, reviewId, OK);
		assertEquals(1, repository.count());

		deleteAndVerifyReviewsByProductId(productId, OK);
		assertEquals(0, repository.count());

		deleteAndVerifyReviewsByProductId(productId, OK); // idempotence
	}

	@Test
	void getReviewsMissingParameter() {
		getAndVerifyReviewsByProductId("", HttpStatus.BAD_REQUEST)
			.jsonPath("$.path").isEqualTo("/review")
			.jsonPath("$.message").isEqualTo("Required query parameter 'productId' is not present.");
	}

	@Test
	void getReviewsInvalidParameter() {
		getAndVerifyReviewsByProductId("?productId=no-integer", HttpStatus.BAD_REQUEST)
			.jsonPath("$.path").isEqualTo("/review")
			.jsonPath("$.message").isEqualTo("Type mismatch.");
	}

	@Test
	void getReviewsNotFound() {
		getAndVerifyReviewsByProductId("?productId=42", HttpStatus.OK)
			.jsonPath("$.length()").isEqualTo(0);
	}

	@Test
	void getReviewsInvalidParameterNegativeValue() {
		int productIdInvalid = -1;
		getAndVerifyReviewsByProductId(productIdInvalid, HttpStatus.UNPROCESSABLE_ENTITY)
			.jsonPath("$.path").isEqualTo("/review")
			.jsonPath("$.message").isEqualTo("Invalid productId: " + productIdInvalid);
	}


	private WebTestClient.BodyContentSpec getAndVerifyReviewsByProductId(int productId, HttpStatus expectedStatus) {
		return getAndVerifyReviewsByProductId("?productId=" + productId, expectedStatus);
	}

	private WebTestClient.BodyContentSpec getAndVerifyReviewsByProductId(String productIdQuery, HttpStatus expectedStatus) {
		return client.get()
			.uri("/review" + productIdQuery)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}

	private WebTestClient.BodyContentSpec postAndVerifyReview(int productId, int reviewId, HttpStatus expectedStatus) {
		Review review = new Review(productId, reviewId, "Author " + reviewId, "Subject " + reviewId, "Content " + reviewId, "SA");
		return client.post()
			.uri("/review")
			.body(just(review), Review.class)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}

	private WebTestClient.BodyContentSpec deleteAndVerifyReviewsByProductId(int productId, HttpStatus expectedStatus) {
		return client.delete()
			.uri("/review?productId=" + productId)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectBody();
	}

}
