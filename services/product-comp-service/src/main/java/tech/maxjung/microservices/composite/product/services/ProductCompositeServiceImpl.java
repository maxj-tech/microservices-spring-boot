package tech.maxjung.microservices.composite.product.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import tech.maxjung.api.composite.product.*;
import tech.maxjung.api.core.product.Product;
import tech.maxjung.api.core.recommendation.Recommendation;
import tech.maxjung.api.core.review.Review;
import tech.maxjung.api.exceptions.NotFoundException;
import tech.maxjung.util.http.ServiceUtil;

import java.util.Collections;
import java.util.List;

@RestController
public class ProductCompositeServiceImpl implements ProductCompositeService {

	private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeServiceImpl.class);

	private final ServiceUtil serviceUtil;
	private final ProductCompositeIntegration integration;

	public ProductCompositeServiceImpl(ServiceUtil serviceUtil, ProductCompositeIntegration integration) {
		this.serviceUtil = serviceUtil;
		this.integration = integration;
	}

	@Override
	public ProductAggregate getProduct(int productId) {

		var product = integration.getProduct(productId);
		if (product == null) {
			throw new NotFoundException("No product found for productId: " + productId);
		}

		List<Recommendation> recommendations = integration.getRecommendations(productId);
		List<Review> reviews = integration.getReviews(productId);
		return createProductAggregate(product, recommendations, reviews, serviceUtil.getServiceAddress());
	}

	@Override
	public void createProduct(ProductAggregate productAggr) {
		try {
			LOG.debug("createProduct: creates a new composite entity for productId: {}", productAggr.productId());

			Product product = new Product(productAggr.productId(), productAggr.name(), productAggr.weight(), null);
			integration.createProduct(product);

			if (productAggr.recommendations() != null) {
				for (RecommendationSummary r : productAggr.recommendations()) {
					integration.createRecommendation(
						new Recommendation(productAggr.productId(), r.recommendationId(), r.author(), r.rate(), r.content(), null)
					);
				}
			}

			if (productAggr.reviews() != null) {
				for (ReviewSummary r : productAggr.reviews()) {
					integration.createReview(
						new Review(productAggr.productId(), r.reviewId(), r.author(), r.subject(), r.content(), null)
					);
				}
			}

			LOG.debug("createProduct: created a new composite entity for productId: {}", productAggr.productId());

		} catch (RuntimeException re) {
			LOG.warn("createCompositeProduct failed", re);
			throw re;
		}
	}

	@Override
	public void deleteProduct(int productId) {
		integration.deleteProduct(productId);
		integration.deleteRecommendations(productId);
		integration.deleteReviews(productId);
	}

	private ProductAggregate createProductAggregate(
		Product product,
		List<Recommendation> recommendations,
		List<Review> reviews,
		String serviceAddress
	) {
		List<RecommendationSummary> recommendationSummaries = convertRecommendations(recommendations);
		List<ReviewSummary> reviewSummaries = convertReviews(reviews);

		ServiceAddresses serviceAddresses = createServiceAddresses(product, reviews, recommendations, serviceAddress);

		return new ProductAggregate(
			product.productId(),
			product.name(),
			product.weight(),
			recommendationSummaries,
			reviewSummaries,
			serviceAddresses
		);
	}


	private List<RecommendationSummary> convertRecommendations(List<Recommendation> recommendations) {
		if (recommendations == null) {
			return Collections.emptyList();
		}
		return recommendations.stream()
			.map(r -> new RecommendationSummary(r.recommendationId(), r.author(), r.rate(), r.content())).toList();
	}

	private List<ReviewSummary> convertReviews(List<Review> reviews) {
		if (reviews == null) {
			return Collections.emptyList();
		}
		return reviews.stream()
			.map(r -> new ReviewSummary(r.reviewId(), r.author(), r.subject(), r.content())).toList();
	}

	private ServiceAddresses createServiceAddresses(Product product, List<Review> reviews, List<Recommendation> recommendations, String serviceAddress) {
		String productAddress = product.serviceAddress();
		String reviewAddress = !reviews.isEmpty() ? reviews.get(0).serviceAddress() : "";
		String recommendationAddress = !recommendations.isEmpty() ? recommendations.get(0).serviceAddress() : "";

		return new ServiceAddresses(serviceAddress, productAddress, reviewAddress, recommendationAddress);
	}

}
