package tech.maxjung.microservices.core.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("tech.maxjung")
public class ProductServiceApplication {

	private static final Logger LOG = LoggerFactory.getLogger(ProductServiceApplication.class);

	public static void main(String[] args) {

		ConfigurableApplicationContext context = SpringApplication.run(ProductServiceApplication.class, args);

		String host = context.getEnvironment().getProperty("spring.data.mongodb.host");
		String port = context.getEnvironment().getProperty("spring.data.mongodb.port");

		LOG.info("Connected to MongoDb: {}:{}", host, port);

	}

}
