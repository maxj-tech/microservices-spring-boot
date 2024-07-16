package tech.maxjung.microservices.core.review.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;
import tech.maxjung.api.core.review.Review;
import tech.maxjung.api.core.review.ReviewService;
import tech.maxjung.api.exceptions.InvalidInputException;
import tech.maxjung.microservices.core.review.persistence.ReviewEntity;
import tech.maxjung.microservices.core.review.persistence.ReviewRepository;
import tech.maxjung.util.http.ServiceUtil;

import java.util.List;

@RestController
public class ReviewServiceImpl implements ReviewService {

	private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);

	private final ReviewRepository repository;

	private final ReviewMapper mapper;

	private final ServiceUtil serviceUtil;

	public ReviewServiceImpl(ReviewRepository repository, ReviewMapper mapper, ServiceUtil serviceUtil) {
		this.repository = repository;
		this.mapper = mapper;
		this.serviceUtil = serviceUtil;
	}

	@Override
	public List<Review> getReviews(int productId) {
		if (productId < 1) {
			throw new InvalidInputException("Invalid productId: " + productId);
		}

		List<ReviewEntity> reviewEntities = repository.findByProductId(productId);
		List<Review> reviewApis = mapper.entitiesToApis(reviewEntities, serviceUtil.getServiceAddress());

		LOG.debug("/reviews response size: {}", reviewApis.size());
		return reviewApis;
	}

	@Override
	public Review createReview(Review review) {
		try {
			ReviewEntity reviewEntity = repository.save(mapper.apiToEntity(review));
			LOG.debug("createReview: entity created for {}", getKeyString(review));

			String serviceAddress = serviceUtil.getServiceAddress();
			return mapper.entityToApi(reviewEntity, serviceAddress);
		} catch (DataIntegrityViolationException dive) {
			throw new InvalidInputException("Duplicate key " + getKeyString(review));
		}
	}

	@Override
	public void deleteReviews(int productId) {
		LOG.debug("deleteReviews: tries to delete reviews for the product with productId: {}", productId);
		repository.deleteAll(repository.findByProductId(productId));
	}

	private static String getKeyString(Review r) {
		return String.format("(ProductId,ReviewId): (%d, %d)", r.productId(), r.reviewId());
	}
}
