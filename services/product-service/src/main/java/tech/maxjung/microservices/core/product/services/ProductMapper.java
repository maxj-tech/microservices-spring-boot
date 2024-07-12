package tech.maxjung.microservices.core.product.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import tech.maxjung.api.core.product.Product;
import tech.maxjung.microservices.core.product.persistence.ProductEntity;

@Mapper(componentModel = "spring")
public abstract class ProductMapper {

	public Product entityToApi(ProductEntity entity, String serviceAddress) {
		return new Product(entity.getProductId(), entity.getName(), entity.getWeight(), serviceAddress);
	}

	@Mappings({
		@Mapping(target = "id", ignore = true),
		@Mapping(target = "version", ignore = true)
	})
	public abstract ProductEntity apiToEntity(Product api);
}
