package tech.maxjung.microservices.core.recommendation;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import tech.maxjung.api.core.recommendation.Recommendation;
import tech.maxjung.microservices.core.recommendation.persistence.RecommendationEntity;
import tech.maxjung.microservices.core.recommendation.services.RecommendationMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RecommendationMapperTest {

	private final RecommendationMapper mapper = Mappers.getMapper(RecommendationMapper.class);

	private final static String SRVC_ADDRESS = "sa";

	@Test
	void entityToApi() {

		assertNotNull(mapper);

		Recommendation api = new Recommendation(1, 1, "a", 1, "c", SRVC_ADDRESS);

		RecommendationEntity entity = mapper.apiToEntity(api);

		verifyEquality(api, entity);
	}

	@Test
	void apiToEntity() {

		assertNotNull(mapper);

		RecommendationEntity entity = new RecommendationEntity(1, 1, "a", 1, "c");
		Recommendation api = mapper.entityToApi(entity, SRVC_ADDRESS);

		verifyEquality(api, entity);
	}

	private static void verifyEquality(Recommendation api, RecommendationEntity entity) {
		assertEquals(api.productId(), entity.getProductId());
		assertEquals(api.recommendationId(), entity.getRecommendationId());
		assertEquals(api.author(), entity.getAuthor());
		assertEquals(api.rate(), entity.getRating());
		assertEquals(api.content(), entity.getContent());
		assertEquals(api.serviceAddress(), SRVC_ADDRESS);
	}

}
