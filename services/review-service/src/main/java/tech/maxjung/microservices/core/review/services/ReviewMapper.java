package tech.maxjung.microservices.core.review.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tech.maxjung.api.core.review.Review;
import tech.maxjung.microservices.core.review.persistence.ReviewEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ReviewMapper {

	public Review entityToApi(ReviewEntity entity, String srvcAddress) {
		return new Review(
			entity.getProductId(),
			entity.getReviewId(),
			entity.getAuthor(),
			entity.getSubject(),
			entity.getContent(),
			srvcAddress);
	}

	public List<Review> entitiesToApis(List<ReviewEntity> entities, String serviceAddress) {
		return entities.stream().map(entity -> entityToApi(entity, serviceAddress)).toList();
	}

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "version", ignore = true)
	public abstract ReviewEntity apiToEntity(Review api);
}
