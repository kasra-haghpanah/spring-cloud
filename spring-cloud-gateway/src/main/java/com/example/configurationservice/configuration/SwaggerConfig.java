package com.example.configurationservice.configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springdoc.core.AbstractSwaggerUiConfigProperties;
import org.springdoc.core.SwaggerUiConfigParameters;
import org.springdoc.core.SwaggerUiConfigProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Objects;

@Configuration
public class SwaggerConfig {

    @Bean
    public SwaggerUiConfigParameters swaggerUiConfigParameters() {
        SwaggerUiConfigProperties properties = new SwaggerUiConfigProperties();
        //properties.setGroupsOrder(AbstractSwaggerUiConfigProperties.Direction.ASC);
        return new SwaggerUiConfigParameters(properties);
    }

    @Bean
    public CommandLineRunner openApiGroups(
            RouteDefinitionLocator locator//,
            //SwaggerUiConfigParameters swaggerUiParameters
    ) {
        return args -> Objects.requireNonNull(locator
                        .getRouteDefinitions().collectList().block())
                .stream()
                .map(RouteDefinition::getId)
                .filter(id -> {
                    return id.matches(".*-service");
                })
                .map(id -> {
                   return id.replace("-service", "");
                })
                .forEach(swaggerUiConfigParameters()::addGroup);
    }


    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> {
            factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                    .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                    .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(7)).build())
                    .circuitBreakerConfig(CircuitBreakerConfig.custom().failureRateThreshold(10)
                            .slowCallRateThreshold(5)
                            .slowCallDurationThreshold(Duration.ofSeconds(3)).build()).build());
        };
    }

    //@Bean
//    public GroupedOpenApi customerApi() {
//        return GroupedOpenApi.builder()
//                .pathsToMatch("/customer/**")
//                .group("customer")
//                .build();
//    }

}
