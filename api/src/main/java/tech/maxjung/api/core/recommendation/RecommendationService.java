package tech.maxjung.api.core.recommendation;

import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface RecommendationService {

	/**
	 * Sample usage: "curl $HOST:$PORT/recommendation?productId=1".
	 *
	 * @param productId Id of the product to look up recommendations for
	 * @return the recommendations of the product
	 */
	@GetMapping(
		value = "/recommendation",
		produces = "application/json")
	List<Recommendation> getRecommendations(@RequestParam(value = "productId", required = true) int productId);

	/**
	 * Sample usage: curl -X POST $HOST:$PORT/recommendation \
	 * --json '{"productId":123,"recommendationId":456,"author":"me","rate":5,"content":"yada, yada, yada"}'
	 *
	 * @param recommendation A JSON representation of the new recommendation
	 * @return A JSON representation of the newly created recommendation
	 */
	@PostMapping(
		value = "/recommendation",
		consumes = "application/json",
		produces = "application/json")
	Recommendation createRecommendation(@RequestBody Recommendation recommendation);


	/**
	 * Sample usage: "curl -X DELETE $HOST:$PORT/recommendation?productId=1".
	 *
	 * @param productId Id of the product to delete recommendations for
	 */
	@DeleteMapping(value = "/recommendation")
	void deleteRecommendations(@RequestParam(value = "productId", required = true) int productId);
}
