package tech.maxjung.microservices.composite.product.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import tech.maxjung.api.core.product.Product;
import tech.maxjung.api.core.product.ProductService;
import tech.maxjung.api.core.recommendation.Recommendation;
import tech.maxjung.api.core.recommendation.RecommendationService;
import tech.maxjung.api.core.review.Review;
import tech.maxjung.api.core.review.ReviewService;
import tech.maxjung.api.exceptions.InvalidInputException;
import tech.maxjung.api.exceptions.NotFoundException;
import tech.maxjung.util.http.HttpErrorInfo;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@Component
public class ProductCompositeIntegration implements ProductService, RecommendationService, ReviewService {

	private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeIntegration.class);

	private final RestTemplate restTemplate;
	private final ObjectMapper mapper;

	private final String productServiceUrl;
	private final String recommendationServiceUrl;
	private final String reviewServiceUrl;


	public ProductCompositeIntegration(
		RestTemplate restTemplate,
		ObjectMapper mapper,

		@Value("${app.product-service.host}") String productServiceHost,
		@Value("${app.product-service.port}") int productServicePort,

		@Value("${app.recommendation-service.host}") String recommendationServiceHost,
		@Value("${app.recommendation-service.port}") int recommendationServicePort,

		@Value("${app.review-service.host}") String reviewServiceHost,
		@Value("${app.review-service.port}") int reviewServicePort
	) {
		this.restTemplate = restTemplate;
		this.mapper = mapper;

		this.productServiceUrl =
			"http://" + productServiceHost + ":" + productServicePort + "/product/";
		this.recommendationServiceUrl =
			"http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendation?productId=";
		this.reviewServiceUrl =
			"http://" + reviewServiceHost + ":" + reviewServicePort + "/review?productId=";
	}


	@Override
	public Product getProduct(int productId) {
		try {
			String url = productServiceUrl + productId;
			LOG.debug("Will call getProduct API on URL: {}", url);

			Product product = restTemplate.getForObject(url, Product.class);
			LOG.debug("Found a product with id: {}", product.productId());

			return product;

		} catch (HttpClientErrorException ex) {

			// the reverse of GlobalControllerExceptionHandler's code
			switch (HttpStatus.resolve(ex.getStatusCode().value())) {
				case NOT_FOUND:
					throw new NotFoundException(getErrorMessage(ex));

				case UNPROCESSABLE_ENTITY:
					throw new InvalidInputException(getErrorMessage(ex));

				default:
					LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
					LOG.warn("Error body: {}", ex.getResponseBodyAsString());
					throw ex;
			}
		}
	}

	@Override
	public Product createProduct(Product product) {
		throw new NotImplementedException("Not yet implemented"); // FIXME
	}

	@Override
	public void deleteProduct(int productId) {
		throw new NotImplementedException("Not yet implemented"); // FIXME
	}

	@Override
	public List<Recommendation> getRecommendations(int productId) {
		try {
			String url = recommendationServiceUrl + productId;

			LOG.debug("Will call getRecommendations API on URL: {}", url);

            /* Using exchange() over getForObject() because it handles JSON to List<Recommendation> conversion.
               This is needed due to Java's type erasure with generics, which ParameterizedTypeReference helps resolve
                by retaining type information at runtime for correct mapping. */
			List<Recommendation> recommendations = restTemplate
				.exchange(url, GET, null, new ParameterizedTypeReference<List<Recommendation>>() {
				})
				.getBody();

			LOG.debug("Found {} recommendations for a product with id: {}", recommendations.size(), productId);
			return recommendations;

		} catch (Exception ex) {
			LOG.warn("Got an exception while requesting recommendations, return empty list: {}", ex.getMessage());
			return Collections.emptyList();
		}
	}

	@Override
	public Recommendation createRecommendation(Recommendation recommendation) {
		throw new NotImplementedException("Not yet implemented"); // FIXME
	}

	@Override
	public void deleteRecommendations(int productId) {
		throw new NotImplementedException("Not yet implemented"); // FIXME
	}

	@Override
	public List<Review> getReviews(int productId) {
		try {
			String url = reviewServiceUrl + productId;

			LOG.debug("Will call getReviews API on URL: {}", url);
			List<Review> reviews = restTemplate
				.exchange(url, GET, null, new ParameterizedTypeReference<List<Review>>() {
				})
				.getBody();

			LOG.debug("Found {} reviews for a product with id: {}", reviews.size(), productId);
			return reviews;

		} catch (Exception ex) {
			LOG.warn("Got an exception while requesting reviews, return empty list: {}", ex.getMessage());
			return Collections.emptyList();
		}
	}


	private String getErrorMessage(HttpClientErrorException ex) {
		try {
			return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).message();
		} catch (IOException ioex) {
			return ex.getMessage();
		}
	}
}
