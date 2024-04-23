package tech.maxjung.microservices.core.recommendation.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import tech.maxjung.api.core.recommendation.Recommendation;
import tech.maxjung.api.core.recommendation.RecommendationService;
import tech.maxjung.api.exceptions.InvalidInputException;
import tech.maxjung.tech.maxjung.util.http.ServiceUtil;

import java.util.Collections;
import java.util.List;

@RestController
public class RecommendationServiceImpl implements RecommendationService {

    private static final Logger LOG = LoggerFactory.getLogger(RecommendationServiceImpl.class);

    private final ServiceUtil serviceUtil;

    public RecommendationServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<Recommendation> getRecommendations(int productId) {

        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        if (productId == 113) {
            LOG.debug("No recommendations found for productId: {}", productId);
            return Collections.emptyList();
        }

        var recommendations = List.of(
                new Recommendation(productId, 1, "Author 1", 1, "Content 1", serviceUtil.getServiceAddress()),
                new Recommendation(productId, 2, "Author 2", 2, "Content 2", serviceUtil.getServiceAddress()),
                new Recommendation(productId, 3, "Author 3", 3, "Content 3", serviceUtil.getServiceAddress())
        );

        LOG.debug("/recommendation response size: {}", recommendations.size());

        return recommendations;
    }
}
