package com.tsys.fraud_checker.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

@Configuration
public class SwaggerConfiguration {
//  @Bean
//  public Docket api() {
//    return new Docket(DocumentationType.OAS_30)
//        // select() method returns an instance of ApiSelectorBuilder,
//        // which provides a way to control the endpoints exposed by Swagger.
//        .select()
//        // configure predicates for selecting RequestHandlers with the help
//        // of RequestHandlerSelectors and PathSelectors.
//        //
//        // Using any() for both will make documentation for our entire
//        // API available through Swagger.
//        .apis(RequestHandlerSelectors.any())
//        // Exclude Spring BasicErrorController from the path
//        .paths(Predicate.not(PathSelectors.regex("/error.*")))
//        .build()
//        .apiInfo(apiInfo())
//        .produces(Set.of("application/json"))
//        .consumes(Set.of("application/json"));
//  }
//
//  private ApiInfo apiInfo() {
//    final ApiInfo apiInfo = new ApiInfo(
//        "FraudChecker REST API",
//        "Checks for Credit Card Frauds.",
//        "API v1.0",
//        "https://www.gnu.org/licenses/gpl-3.0.html",
//        new Contact("Dhaval Dalal","https://dhavaldalal.wordpress.com", "dhaval@dalal.com"),
//        "Copyleft License",
//        "https://en.wikipedia.org/wiki/Copyleft",
//        Collections.emptyList());
//    return apiInfo;
//  }

//  @Bean
//  GroupedOpenApi publicApi() {
//    return GroupedOpenApi.builder()
//        .group("public-apis")
//        .pathsToMatch("/**")
//        .build();
//  }

  @Bean
  OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(info())
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
        .components(
            new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")));
  }

    private Info info() {
    final Info info = new Info()
        .title("FraudChecker REST API")
        .description("Checks for Credit Card Frauds.")
        .version("v1.0")
        .license(new License().url("https://www.gnu.org/licenses/gpl-3.0.html").name("Copyleft License"))
        .contact(new Contact().name("Dhaval Dalal").url("https://github.com/DhavalDalal/fraud-checker-service").email("dhaval@dalal.com"));
//            .
//        .,
//        "https://en.wikipedia.org/wiki/Copyleft",
//        Collections.emptyList();
    return info;
  }

}
