package tech.maxjung.microservices.core.review.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import tech.maxjung.api.core.review.Review;
import tech.maxjung.api.core.review.ReviewService;
import tech.maxjung.api.exceptions.InvalidInputException;
import tech.maxjung.tech.maxjung.util.http.ServiceUtil;

import java.util.Collections;
import java.util.List;

@RestController
public class ReviewServiceImpl implements ReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ServiceUtil serviceUtil;

    public ReviewServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<Review> getReviews(int productId) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        if (productId == 213) {
            LOG.debug("No reviews found for productId: {}", productId);
            return Collections.emptyList();
        }

        List<Review> reviews = List.of(
                new Review(productId, 1, "Author 1", "Subject 1", "Content 1", serviceUtil.getServiceAddress()),
                new Review(productId, 2, "Author 2", "Subject 2", "Content 2", serviceUtil.getServiceAddress()),
                new Review(productId, 3, "Author 3", "Subject 3", "Content 3", serviceUtil.getServiceAddress())
        );

        LOG.debug("/reviews response size: {}", reviews.size());

        return reviews;
    }
}
