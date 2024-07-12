package tech.maxjung.microservices.core.product;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import tech.maxjung.api.core.product.Product;
import tech.maxjung.microservices.core.product.persistence.ProductEntity;
import tech.maxjung.microservices.core.product.services.ProductMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProductMapperTest {

	private final ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

	@Test
	void entityToApi() {

		assertNotNull(mapper);

		Product api = new Product(1, "n", 1, "sa");

		ProductEntity entity = mapper.apiToEntity(api);

		assertEquals(api.productId(), entity.getProductId());
		assertEquals(api.name(), entity.getName());
		assertEquals(api.weight(), entity.getWeight());
		assertEquals(api.serviceAddress(), "sa");
	}

	@Test
	void apiToEntity() {

		assertNotNull(mapper);

		ProductEntity entity = new ProductEntity(1, "n", 1);
		Product api = mapper.entityToApi(entity, "sa");

		assertEquals(api.productId(), entity.getProductId());
		assertEquals(api.name(), entity.getName());
		assertEquals(api.weight(), entity.getWeight());
		assertEquals(api.serviceAddress(), "sa");
	}

}
