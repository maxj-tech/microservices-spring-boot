package tech.maxjung.api.core.review;

import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface ReviewService {

  /**
   * Sample usage: "curl $HOST:$PORT/review?productId=1".
   *
   * @param productId Id of the product for which to get the reviews
   * @return the reviews of the product
   */
  @GetMapping(
    value = "/review",
    produces = "application/json")
  List<Review> getReviews(@RequestParam(value = "productId", required = true) int productId);

  /**
   * Sample usage: curl -X POST $HOST:$PORT/review \
   * --json '{"productId":123,"reviewId":456,"author":"me","subject":"like it!","content":"lore ipsum"}'
   *
   * @param review A JSON representation of the new review
   * @return A JSON representation of the newly created review
   */
  @PostMapping(
    value = "/review",
    consumes = "application/json",
    produces = "application/json")
  Review createReview(@RequestBody Review review);


  /**
   * Sample usage: "curl -X DELETE $HOST:$PORT/review?productId=1".
   *
   * @param productId Id of the product to delete recommendations for
   */
  @DeleteMapping(value = "/review")
  void deleteReviews(@RequestParam(value = "productId", required = true) int productId);
}
