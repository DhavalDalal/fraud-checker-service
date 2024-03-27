package com.tsys.fraud_checker.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfiguration {
  private final License copyleft = new License()
      .url("https://www.gnu.org/licenses/gpl-3.0.html")
      .name("Copyleft License");
  private final Contact dhaval_dalal = new Contact()
      .name("Dhaval Dalal")
      .url("https://github.com/DhavalDalal/fraud-checker-service")
      .email("dhaval@softwareartisan.com");

  private final String version = "v1.0";

  @Bean
  GroupedOpenApi fraudCheckApi() {
    return GroupedOpenApi.builder()
        .group("fraud-check")
        .displayName("Fraud Checking APIs") // shows on the selection menu on the UI
        .pathsToMatch("/**")
        .pathsToExclude("/validate**/*", "/validate**")
        .packagesToScan("com.tsys.fraud_checker.web")
        .packagesToExclude("com.tsys.fraud_checker.web.internal")
        .build();
  }

  @Bean
  GroupedOpenApi validationApi() {
    return GroupedOpenApi.builder()
        .group("validation-apis")
        .displayName("Validation APIs")
        .addOpenApiCustomizer(openApi -> {
          final Info info = new Info()
              .title("Validation APIs on Fraud Checker Service")
              .description("1. Validate Request Parameter.\n" +
                  "2. Validate Header Parameter.\n" +
                  "3. Validate Header Parameter Using POST.\n" +
                  "4. Validate Path Variable.")
              .version(version)
              .license(copyleft)
              .contact(dhaval_dalal);
          openApi.info(info);
        })
        .pathsToMatch("/validate**/*", "/validate**")
        .packagesToScan("com.tsys.fraud_checker.web")
        .build();
  }

  @Bean
  GroupedOpenApi testApi() {
    return GroupedOpenApi.builder()
        .group("test-setup")
        .displayName("Test Setup and Routing APIs")
        .addOpenApiCustomizer(openApi -> {
          final Info info = new Info()
              .title("Set Up Fraud Checker Service APIs For Testing")
              .description("1. Setup Router to route to real or stub service.\n" +
                  "2. Setup stubbed requests and responses.\n" +
                  "3. Setup up delay in responses.")
              .version(version)
              .license(copyleft)
              .contact(dhaval_dalal);
          openApi.info(info);
        })
        .packagesToScan("com.tsys.fraud_checker.web.internal")
        .build();
  }

  @Bean
  OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(info())
//        .components(new Components().addSecuritySchemes("basicScheme",
//          new SecurityScheme().type(SecurityScheme.Type.OAUTH2).scheme("OAUTH2")))
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
        .components(
            new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT"))
        );
  }

  @Bean
  public OpenApiCustomizer openApiAdditionalPropertiesCustomiser() {
    return openApi -> {
      openApi
          .getComponents()
          .getSchemas()
          .values()
          .forEach(schema -> {
            schema.setAdditionalProperties(false);
            // Set Default Response
//            schema.set();
          });

      openApi
          .getPaths()
          .values().stream()
          .flatMap(pathItem -> pathItem.readOperations().stream())
          .forEach(operation -> {
            operation.getResponses().put("429", new ApiResponse() {{
              setDescription("Too Many Requests");
            }});
            operation.getResponses().put("401", new ApiResponse() {{
              setDescription("Unauthorized");
              setContent(new Content().addMediaType("application/json", new MediaType()
                  .schema(new Schema().$ref("#/components/schemas/Error"))));

            }});
            operation.getResponses().put("403", new ApiResponse() {{
              setDescription("Forbidden");
              setContent(new Content().addMediaType("application/json", new MediaType()
                  .schema(new Schema().$ref("#/components/schemas/Error"))));
            }});
            operation.getResponses().put("default", new ApiResponse() {{
              setDescription("unexpected error");
              setContent(new Content().addMediaType("application/json", new MediaType()
                  .schema(new Schema().$ref("#/components/schemas/Error"))));
            }});
          });

      final var _404HttpMethods = List.of(PathItem.HttpMethod.GET, PathItem.HttpMethod.PUT, PathItem.HttpMethod.HEAD, PathItem.HttpMethod.DELETE);
      openApi
          .getPaths()
          .values().stream()
          .flatMap(pathItem -> pathItem.readOperationsMap().entrySet().stream().filter(entry -> {
                var httpMethod = entry.getKey();
                return _404HttpMethods.stream().anyMatch(_404httpMethod -> _404httpMethod == httpMethod);
              })
              .map(entry -> entry.getValue()))
          .forEach(operation -> {
            operation.getResponses().put("404", new ApiResponse() {{
              setDescription("Not Found!");
              setContent(new Content().addMediaType("application/json", new MediaType()
                  .schema(new Schema().$ref("#/components/schemas/Error"))));

            }});
          });

      final var _406HttpMethods = List.of(PathItem.HttpMethod.GET, PathItem.HttpMethod.POST, PathItem.HttpMethod.PUT, PathItem.HttpMethod.PATCH);
      openApi
          .getPaths()
          .values().stream()
          .flatMap(pathItem -> pathItem.readOperationsMap().entrySet().stream().filter(entry -> {
                var httpMethod = entry.getKey();
                return _406HttpMethods.stream().anyMatch(_406httpMethod -> _406httpMethod == httpMethod);
              })
              .map(entry -> entry.getValue()))
          .forEach(operation ->
              operation.getResponses().put("406", new ApiResponse() {{
                setDescription("Not Acceptable");
                setContent(new Content().addMediaType("application/json", new MediaType()
                    .schema(new Schema().$ref("#/components/schemas/Error"))));
              }})
          );

    };
  }

  private Info info() {
    final Info info = new Info()
        .title("Fraud Checker Service APIs")
        .description("Checks for Credit Card Frauds.")
        .version(version)
        .license(copyleft)
        .contact(dhaval_dalal);
    return info;
  }
}