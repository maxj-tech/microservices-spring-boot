package tech.maxjung.microservices.core.recommendation.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tech.maxjung.api.core.recommendation.Recommendation;
import tech.maxjung.microservices.core.recommendation.persistence.RecommendationEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class RecommendationMapper {

	public Recommendation entityToApi(RecommendationEntity entity, String serviceAddress) {
		return new Recommendation(
			entity.getProductId(),
			entity.getRecommendationId(),
			entity.getAuthor(),
			entity.getRating(),
			entity.getContent(),
			serviceAddress);
	}

	public List<Recommendation> entitiesToApis(List<RecommendationEntity> entities, String serviceAddress) {
		return entities.stream().map(entity -> entityToApi(entity, serviceAddress)).toList();
	}

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "version", ignore = true)
	@Mapping(target = "rating", source = "rate")
	public abstract RecommendationEntity apiToEntity(Recommendation api);


}
