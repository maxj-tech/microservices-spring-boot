package tech.maxjung.microservices.composite.product.services;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Objects;

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

		@Value("${app.product-service.host}") String productSvcHost,
		@Value("${app.product-service.port}") int productSvcPort,

		@Value("${app.recommendation-service.host}") String recommendationSvcHost,
		@Value("${app.recommendation-service.port}") int recommendationSvcPort,

		@Value("${app.review-service.host}") String reviewSvcHost,
		@Value("${app.review-service.port}") int reviewSvcPort
	) {
		this.restTemplate = restTemplate;
		this.mapper = mapper;

		this.productServiceUrl = "http://" + productSvcHost + ":" + productSvcPort + "/product";
		this.recommendationServiceUrl = "http://" + recommendationSvcHost + ":" + recommendationSvcPort + "/recommendation";
		this.reviewServiceUrl = "http://" + reviewSvcHost + ":" + reviewSvcPort + "/review";
	}


	@Override
	public Product getProduct(int productId) {
		try {
			String url = productServiceUrl + "/" + productId;
			LOG.debug("Will call getProduct API on URL: {}", url);
			Product product = restTemplate.getForObject(url, Product.class);
			LOG.debug("Found a product with id: {}", product.productId());
			return product;
		} catch (HttpClientErrorException ex) {
			throw handleHttpClientException(ex);
		}
	}

	@Override
	public Product createProduct(Product product) {
		try {
			LOG.debug("Will post a new product to URL: {}", productServiceUrl);
			Product p = restTemplate.postForObject(productServiceUrl, product, Product.class);
			LOG.debug("Created a product with id: {}", p.productId());
			return p;
		} catch (HttpClientErrorException ex) {
			throw handleHttpClientException(ex);
		}
	}

	@Override
	public void deleteProduct(int productId) {
		try {
			String url = productServiceUrl + "/" + productId;
			LOG.debug("Will call the deleteProduct API on URL: {}", url);
			restTemplate.delete(url);
		} catch (HttpClientErrorException ex) {
			throw handleHttpClientException(ex);
		}
	}

	@Override
	public List<Recommendation> getRecommendations(int productId) {
		try {
			String url = recommendationServiceUrl + "?productId=" + productId;
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
		try {
			String url = recommendationServiceUrl;
			LOG.debug("Will post a new recommendation to URL: {}", url);
			Recommendation rec = restTemplate.postForObject(url, recommendation, Recommendation.class);
			LOG.debug("Created a recommendation with id: {}", rec.recommendationId());
			return rec;
		} catch (HttpClientErrorException ex) {
			throw handleHttpClientException(ex);
		}
	}


	@Override
	public void deleteRecommendations(int productId) {
		try {
			String url = recommendationServiceUrl + "?productId=" + productId;
			LOG.debug("Will call the deleteRecommendations API on URL: {}", url);
			restTemplate.delete(url);
		} catch (HttpClientErrorException ex) {
			throw handleHttpClientException(ex);
		}
	}

	@Override
	public List<Review> getReviews(int productId) {
		try {
			String url = reviewServiceUrl + "?productId=" + productId;
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

	@Override
	public Review createReview(Review review) {
		try {
			String url = reviewServiceUrl;
			LOG.debug("Will post a new review to URL: {}", url);
			Review rev = restTemplate.postForObject(url, review, Review.class);
			LOG.debug("Created a review with id: {}", rev.reviewId());
			return rev;
		} catch (HttpClientErrorException ex) {
			throw handleHttpClientException(ex);
		}
	}

	@Override
	public void deleteReviews(int productId) {
		try {
			String url = reviewServiceUrl + "?productId=" + productId;
			LOG.debug("Will call the deleteReviews API on URL: {}", url);
			restTemplate.delete(url);
		} catch (HttpClientErrorException ex) {
			throw handleHttpClientException(ex);
		}
	}


	private String getErrorMessage(HttpClientErrorException ex) {
		try {
			return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).message();
		} catch (IOException ioex) {
			return ex.getMessage();
		}
	}

	private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
		// the reverse of GlobalControllerExceptionHandler's code
		switch (Objects.requireNonNull(HttpStatus.resolve(ex.getStatusCode().value()))) {

			case NOT_FOUND:
				return new NotFoundException(getErrorMessage(ex));

			case UNPROCESSABLE_ENTITY:
				return new InvalidInputException(getErrorMessage(ex));

			default:
				LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
				LOG.warn("Error body: {}", ex.getResponseBodyAsString());
				return ex;
		}
	}
}
