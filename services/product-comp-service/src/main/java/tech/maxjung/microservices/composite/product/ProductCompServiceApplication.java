package tech.maxjung.microservices.composite.product;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.stream.StreamSupport;

@SpringBootApplication
@ComponentScan("tech.maxjung")
public class ProductCompServiceApplication {

  private static final Logger logger = LoggerFactory.getLogger(ProductCompServiceApplication.class);

  // in order to perform HTTP requests to the core services
  @Bean
  RestTemplate restTemplate() {   // todo will use WebClient for reactive programming later
    return new RestTemplate();
  }

  public static void main(String[] args) {
    SpringApplication.run(ProductCompServiceApplication.class, args);
  }

  @Bean
  CommandLineRunner printProperties(ConfigurableEnvironment env) {
    return args -> {
      logger.debug("Active profiles: {}", Arrays.toString(env.getActiveProfiles()));
      StreamSupport.stream(env.getPropertySources().spliterator(), false)
          .filter(ps -> ps.getSource() instanceof java.util.Map)
          .forEach(ps -> {
            logger.debug("Property source: {}", ps.getName());
            ((java.util.Map<?, ?>) ps.getSource()).forEach((key, value) -> logger.debug("{}: {}", key, value));
          });
    };
  }


  @Value("${api.common.version}")         String apiVersion;
  @Value("${api.common.title}")           String apiTitle;
  @Value("${api.common.description}")     String apiDescription;
  @Value("${api.common.termsOfService}")  String apiTermsOfService;
  @Value("${api.common.license}")         String apiLicense;
  @Value("${api.common.licenseUrl}")      String apiLicenseUrl;
  @Value("${api.common.externalDocDesc}") String apiExternalDocDesc;
  @Value("${api.common.externalDocUrl}")  String apiExternalDocUrl;
  @Value("${api.common.contact.name}")    String apiContactName;
  @Value("${api.common.contact.url}")     String apiContactUrl;
  @Value("${api.common.contact.email}")   String apiContactEmail;

  /**
   * Will exposed on $HOST:$PORT/swagger-ui.html
   *
   * @return the common OpenAPI documentation
   */
  @Bean
  public OpenAPI getOpenApiDocumentation() {
    return new OpenAPI()
        .info(new Info().title(apiTitle)
            .description(apiDescription)
            .version(apiVersion)
            .contact(new Contact()
                .name(apiContactName)
                .url(apiContactUrl)
                .email(apiContactEmail))
            .termsOfService(apiTermsOfService)
            .license(new License()
                .name(apiLicense)
                .url(apiLicenseUrl)))
        .externalDocs(new ExternalDocumentation()
            .description(apiExternalDocDesc)
            .url(apiExternalDocUrl));
  }

}
