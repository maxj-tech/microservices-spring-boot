package tech.maxjung.microservices.core.review;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import tech.maxjung.api.core.review.Review;
import tech.maxjung.microservices.core.review.persistence.ReviewEntity;
import tech.maxjung.microservices.core.review.services.ReviewMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ReviewMapperTest {

	private final ReviewMapper mapper = Mappers.getMapper(ReviewMapper.class);

	private final static String SRVC_ADDRESS = "sa";

	@Test
	void entityToApi() {
		assertNotNull(mapper);
		Review api = new Review(1, 1, "a", "s", "c", SRVC_ADDRESS);

		ReviewEntity entity = mapper.apiToEntity(api);
		verifyEquality(api, entity);
	}

	@Test
	void entitiesToApis() {
		assertNotNull(mapper);

		ReviewEntity entity1 = new ReviewEntity(1, 1, "a", "s", "c");
		ReviewEntity entity2 = new ReviewEntity(1, 2, "a", "s", "c");
		List<ReviewEntity> entityReviews = List.of(entity1, entity2);

		List<Review> apiReviews = mapper.entitiesToApis(entityReviews, SRVC_ADDRESS);
		verifyEquality(apiReviews, entityReviews);
	}

	@Test
	void apiToEntity() {
		assertNotNull(mapper);
		ReviewEntity entity = new ReviewEntity(1, 1, "a", "s", "c");

		Review api = mapper.entityToApi(entity, SRVC_ADDRESS);
		verifyEquality(api, entity);
	}

	private static void verifyEquality(List<Review> apiReviews, List<ReviewEntity> reviewEntities) {
		assertEquals(apiReviews.size(), reviewEntities.size());
		for (int i = 0; i < apiReviews.size(); i++) {
			Review api = apiReviews.get(i);
			ReviewEntity entity = reviewEntities.get(i);
			verifyEquality(api, entity);
		}
	}

	private static void verifyEquality(Review api, ReviewEntity entity) {
		assertEquals(api.productId(), entity.getProductId());
		assertEquals(api.reviewId(), entity.getReviewId());
		assertEquals(api.author(), entity.getAuthor());
		assertEquals(api.subject(), entity.getSubject());
		assertEquals(api.content(), entity.getContent());
		assertEquals(api.serviceAddress(), SRVC_ADDRESS);
	}

}
