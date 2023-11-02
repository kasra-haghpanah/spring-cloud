package com.example.customer.configuration;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.method.HandlerMethod;
import reactor.core.publisher.Mono;

import java.util.List;

// https://medium.com/@pubuduc.14/swagger-openapi-specification-3-integration-with-spring-cloud-gateway-part-2-1d670d4ab69a
// https://www.baeldung.com/spring-rest-openapi-documentation
// https://springdoc.org/
@Configuration
//@SecurityScheme(
//        name = "Bearer Authentication",
//        type = SecuritySchemeType.HTTP,
//        bearerFormat = "JWT",
//        scheme = "bearer"
//)
public class OpenApiConfigs {


    @Bean
    public GroupedOpenApi publicApi(Environment environment) {
        String paths[] = {"/**"};
        return GroupedOpenApi.builder()
                .group(environment.getProperty("spring.application.name"))
                .packagesToScan(environment.getProperty("springdoc.packagesToScan"))
                .pathsToMatch(paths)
                .addOperationCustomizer(addGlobalItemToRequest())
                .build();
    }

    @Bean
    public OpenAPI springCustomerOpenAPI() {

        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                //.specVersion(SpecVersion.V31)
                .servers(List.of(new Server().url("/")))
//                .components(
//                        new Components()
//                                .addSecuritySchemes(
//                                        securitySchemeName,
//                                        new SecurityScheme()
//                                                .description("For calling services, you could not add the bearer word at the beginning of your token.")
//                                                .name("Bearer Authentication")
//                                                .type(SecurityScheme.Type.HTTP)
//                                                .scheme("bearer")
//                                                .bearerFormat("JWT")))
//                .security(List.of(new SecurityRequirement().addList(securitySchemeName)))
                .info(
                        new Info()
                                .title("spring-microservice-reactive")
                                .version("v0.0.1")
                                .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                )
                .externalDocs(new ExternalDocumentation()
                        .description("this sample is about a reactive microservice project")
                        .url("https://github.com/kasra-haghpanah/spring-microservice-reactive"));

    }

    @Bean
    public OperationCustomizer addGlobalItemToRequest() {
        return (Operation operation, HandlerMethod handlerMethod) -> {

            Parameter headerParameter = new Parameter().in(ParameterIn.HEADER.toString()).required(false).
                    schema(
                            new StringSchema()._default("en")._enum(List.of("en", "fa"))._default("fa").required(List.of("en"))
                    )
                    .name("Accept-Language");
            operation.addParametersItem(headerParameter);


            Parameter queryParameter = new Parameter().in(ParameterIn.QUERY.name().toString()).required(false).
                    schema(
                            new StringSchema()._default("export")._enum(List.of("import", "export"))._default("export")
                    )
                    .name("general-query");
            operation.addParametersItem(queryParameter);

            return operation;
        };
    }


}
