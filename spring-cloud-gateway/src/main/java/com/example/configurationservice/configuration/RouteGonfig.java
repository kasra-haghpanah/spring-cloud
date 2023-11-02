package com.example.configurationservice.configuration;


import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.function.Consumer;

@Configuration
public class RouteGonfig {

/*    @Bean
    @Primary
    WebSocketClient reactorNetty2WebSocketClient() {
        //return new ReactorNetty2WebSocketClient();
        return new ReactorNettyWebSocketClient();
    }

    @Bean
    @Primary
    public RequestUpgradeStrategy requestUpgradeStrategy() {
        //return new ReactorNetty2RequestUpgradeStrategy();
        return new ReactorNettyRequestUpgradeStrategy();
    }*/


/*    @Bean
    public RouteLocator gateway(SetPathGatewayFilterFactory filterFactory) {

        var route = Route.async()
                .id("test-route")
                .filter(new OrderedGatewayFilter(filterFactory.apply(config -> {
                    config.setTemplate("/customers/");
                }), 1))
                .uri("lb://customers")
                .asyncPredicate(serverWebExchange -> {
                    URI uri = serverWebExchange.getRequest().getURI();
                    String path = uri.getPath();
                    boolean match = path.contains("/customers");
                    return Mono.just(match);
                }).build();

        return () -> Flux.just(route);

    }*/

    @Bean
    public RouteLocator gateway1(RouteLocatorBuilder routeLocatorBuilder) {

        return routeLocatorBuilder
                .routes()
                .route(routeSpec -> {
                    return routeSpec
                            .path("/workers")// path in gateway
                            .filters(gatewayFilterSpec -> {// path in microservice
                                return gatewayFilterSpec.setPath("/workers");
                            })
                            .uri("lb://customer/");
                })

//        id: ${spring.application.name}-service
//        uri: lb://${spring.application.name}
//        predicates:
//        - Path=/${spring.application.name}/**
//         filters:
//         - RewritePath=/${spring.application.name}/(?<path>.*), /$\{path}

//        id: ticket-service
//        uri: "http://localhost:7071/"
//        predicates:
//        - Path=/ticket/* , /v3/api-docs/ticket
//          filters:
//            - name: CircuitBreaker
//              args:
//                name: ticketBreaker
//                fallbackUri: forward:/fb/ticket/

                .route("customer-service", routeSpec -> {
                    return routeSpec.path(
                                    "/customer/*"
                                   //, "/v3/api-docs/customer"
                            )
                            .filters(fs -> {
                                return fs
                                        .setPath("/v3/api-docs/customer")
//                                        .circuitBreaker(cbc -> cbc
//                                                .setName("customerBreaker")
//                                                .setFallbackUri("forward:/fb/customer/")
                                        ;
                            })
                            .uri("lb://customer");
                            //.uri("http://localhost:8181");
                })

                .route(rs -> rs.path("/default").filters(fs -> fs.filter((exchange, chain) -> {
                                    System.out.println("this is your second chance");
                                    return chain.filter(exchange);
                                }))
                                .uri("https://spring.io/guides")
                )
/*                .route(*//*"route_workers",*//* routeSpec -> {
                    return routeSpec.path("/workers")
                            .filters(fs -> {
                                return fs.setPath("/workers").circuitBreaker(cbc -> cbc.setFallbackUri("forward:/default"));
                            })
                            .uri("lb://customers/");
                })*/
/*                .route(*//*"route_error",*//* routeSpec -> {
                    return routeSpec.path("/error/**")
                            .filters(gatewayFilterSpec -> {

                                return gatewayFilterSpec
                                        .rewritePath("/error/(?<segment>.*)", "/error/${segment}");

                                        //.retry(5);
*//*                                return gatewayFilterSpec.retry(
                                        new Consumer<RetryGatewayFilterFactory.RetryConfig>() {
                                            @Override
                                            public void accept(RetryGatewayFilterFactory.RetryConfig retryConfig) {
                                                retryConfig.setRetries(5);
                                                //retryConfig.setStatuses(HttpStatus.BAD_REQUEST);
                                            }
                                        }
                                );*//*
                            })
                            .uri("lb://customers/");
                })
                .route(routeSpec -> {// http://localhost:9999/persons?names=1,2,3,kasra
                    return routeSpec
                            .path("/persons")// path in gateway
                            .filters(gatewayFilterSpec -> {// path in microservice
                                return gatewayFilterSpec.setPath("/users");
                            })
                            .uri("lb://customers/");
                })*/
                .route("websocket_route", routeSpec -> {
                    return routeSpec
                            .path("/websocket")// path in gateway
                            .filters(gatewayFilterSpec -> {// path in microservice
                                return gatewayFilterSpec.setPath("/websocket");
                            })
                            .uri("lb://customers/");
                    //.uri("ws://localhost:8181");
                })
                .route(routeSpec -> {
                    return routeSpec
                            .path("/ws.html")// path in gateway
                            .filters(gatewayFilterSpec -> {// path in microservice
                                return gatewayFilterSpec.setPath("/ws.html");
                            })
                            .uri("lb://customers/");
                })
                .route(routeSpec -> {
                    return routeSpec
                            .path("/hello").and()
                            .host("*.spring.academy").and()
                            .asyncPredicate(serverWebExchange -> {
                                return Mono.just(serverWebExchange.getAttribute("foo"));
                            })
                            .filters(gatewayFilterSpec -> {
                                return gatewayFilterSpec//.setPath("/guides")
                                        .setPath("/courses");
                            })
                            //.uri("https://spring.io/")
                            .uri("https://spring.academy");
                })
                .route(routeSpec -> {
                    return routeSpec
                            .path("/wiki/**")
                            .filters(gatewayFilterSpec -> {
                                return gatewayFilterSpec.rewritePath("/wiki/(?<handle>.*)", "/wiki/${handle}");
                            })
                            .uri("https://nl.wikipedia.org");
                })
                .build();
    }
}
