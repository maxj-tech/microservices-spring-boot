package tech.maxjung.microservices.core.recommendation.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import tech.maxjung.api.core.recommendation.Recommendation;
import tech.maxjung.api.core.recommendation.RecommendationService;
import tech.maxjung.api.exceptions.InvalidInputException;
import tech.maxjung.microservices.core.recommendation.persistence.RecommendationEntity;
import tech.maxjung.microservices.core.recommendation.persistence.RecommendationRepository;
import tech.maxjung.tech.maxjung.util.http.ServiceUtil;

import java.util.List;

@RestController
public class RecommendationServiceImpl implements RecommendationService {

	private static final Logger LOG = LoggerFactory.getLogger(RecommendationServiceImpl.class);

	private final RecommendationRepository repository;

	private final RecommendationMapper mapper;

	private final ServiceUtil serviceUtil;

	public RecommendationServiceImpl(RecommendationRepository repository, RecommendationMapper mapper, ServiceUtil serviceUtil) {
		this.repository = repository;
		this.mapper = mapper;
		this.serviceUtil = serviceUtil;
	}

	@Override
	public List<Recommendation> getRecommendations(int productId) {

		if (productId < 1) {
			throw new InvalidInputException("Invalid productId: " + productId);
		}

		List<RecommendationEntity> recommendationEntities = repository.findByProductId(productId);
		List<Recommendation> recommendationApis = mapper.entitiesToApis(recommendationEntities, serviceUtil.getServiceAddress());

		LOG.debug("/recommendation response size: {}", recommendationApis.size());
		return recommendationApis;
	}

	@Override
	public Recommendation createRecommendation(Recommendation recommendation) {
		try {
			RecommendationEntity recommendationEntity = repository.save(mapper.apiToEntity(recommendation));
			LOG.debug("createRecommendation: entity created for {}", getKeyString(recommendation));

			String serviceAddress = serviceUtil.getServiceAddress();
			return mapper.entityToApi(recommendationEntity, serviceAddress);
		} catch (DuplicateKeyException dke) {
			throw new InvalidInputException("Duplicate key " + getKeyString(recommendation));
		}
	}

	@Override
	public void deleteRecommendations(int productId) {
		LOG.debug("deleteRecommendations: tries to delete recommendations for the product with productId: {}", productId);
		repository.deleteAll(repository.findByProductId(productId));
	}

	private static String getKeyString(Recommendation r) {
		return String.format("(ProductId,RecommendationId): (%d, %d)", r.productId(), r.recommendationId());
	}
}
