package tech.maxjung.microservices.core.recommendation.persistence;


import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RecommendationRepository extends CrudRepository<RecommendationEntity, String> {

	@Transactional(readOnly = true)
	List<RecommendationEntity> findByProductId(int productId);
}
