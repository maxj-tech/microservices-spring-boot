package tech.maxjung.microservices.composite.product.services;

import org.springframework.web.bind.annotation.RestController;
import tech.maxjung.api.composite.product.*;
import tech.maxjung.api.core.product.Product;
import tech.maxjung.api.core.recommendation.Recommendation;
import tech.maxjung.api.core.review.Review;
import tech.maxjung.api.exceptions.NotFoundException;
import tech.maxjung.util.http.ServiceUtil;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ProductCompositeServiceImpl implements ProductCompositeService {

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

    private ProductAggregate createProductAggregate(
            Product product,
            List<Recommendation> recommendations,
            List<Review> reviews,
            String serviceAddress
    ) {
        List<RecommendationSummary> recommendationSummaries = convertToRecommendationSummaries(recommendations);
        List<ReviewSummary> reviewSummaries = convertToReviewSummaries(reviews);

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

    private List<RecommendationSummary> convertToRecommendationSummaries(List<Recommendation> recommendations) {
        return (recommendations == null) ? null : recommendations.stream()
                .map(r -> new RecommendationSummary(r.recommendationId(), r.author(), r.rate()))
                .collect(Collectors.toList());
    }

    private List<ReviewSummary> convertToReviewSummaries(List<Review> reviews) {
        return (reviews == null) ? null : reviews.stream()
                .map(r -> new ReviewSummary(r.reviewId(), r.author(), r.subject()))
                .collect(Collectors.toList());
    }

    private ServiceAddresses createServiceAddresses(Product product, List<Review> reviews, List<Recommendation> recommendations, String serviceAddress) {
        String productAddress = product.serviceAddress();
        String reviewAddress = (reviews != null && !reviews.isEmpty()) ? reviews.get(0).serviceAddress() : "";
        String recommendationAddress = (recommendations != null && !recommendations.isEmpty()) ? recommendations.get(0).serviceAddress() : "";

        return new ServiceAddresses(serviceAddress, productAddress, reviewAddress, recommendationAddress);
    }

}
