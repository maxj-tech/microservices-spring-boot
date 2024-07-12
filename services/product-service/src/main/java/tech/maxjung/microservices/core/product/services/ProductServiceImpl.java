package tech.maxjung.microservices.core.product.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import tech.maxjung.api.core.product.Product;
import tech.maxjung.api.core.product.ProductService;
import tech.maxjung.api.exceptions.InvalidInputException;
import tech.maxjung.api.exceptions.NotFoundException;
import tech.maxjung.microservices.core.product.persistence.ProductEntity;
import tech.maxjung.microservices.core.product.persistence.ProductRepository;
import tech.maxjung.tech.maxjung.util.http.ServiceUtil;

@RestController
public class ProductServiceImpl implements ProductService {

	private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

	private final ProductRepository repository;

	private final ProductMapper mapper;

	private final ServiceUtil serviceUtil;


	public ProductServiceImpl(ProductRepository repository, ProductMapper mapper, ServiceUtil serviceUtil) {
		this.repository = repository;
		this.mapper = mapper;
		this.serviceUtil = serviceUtil;
	}

	@Override
	public Product getProduct(int productId) {
		if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

		LOG.debug("/product return the found product for productId={}", productId);

		String serviceAddress = serviceUtil.getServiceAddress();

		return mapper.entityToApi(
			repository.findByProductId(productId)
				.orElseThrow(() -> new NotFoundException("No product found for productId: " + productId)), serviceAddress);
	}

	@Override
	public Product createProduct(Product product) {
		try {
			ProductEntity productEntity = repository.save(mapper.apiToEntity(product));

			LOG.debug("createProduct: entity created for productId: {}", product.productId());
			String serviceAddress = serviceUtil.getServiceAddress();
			return mapper.entityToApi(productEntity, serviceAddress);
		} catch (DuplicateKeyException dke) {
			throw new InvalidInputException("Duplicate key, Product Id: " + product.productId());
		}

	}

	@Override
	public void deleteProduct(int productId) {
		LOG.debug("deleteProduct: tries to delete product for productId: {}", productId);
		repository.findByProductId(productId)
			.ifPresent(repository::delete);
	}
}
