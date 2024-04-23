package tech.maxjung.microservices.composite.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@ComponentScan("tech.maxjung")
public class ProductCompServiceApplication {

    // in order to perform HTTP requests to the core services
    @Bean
    RestTemplate restTemplate() {   // todo will use WebClient for reactive programming later
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(ProductCompServiceApplication.class, args);
    }

}
