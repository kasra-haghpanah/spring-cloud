package com.example.configurationservice.configuration;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.event.RefreshRoutesResultEvent;
import org.springframework.cloud.gateway.route.CachingRouteLocator;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicBoolean;

@Configuration
public class ListenerConfig {

    private final AtomicBoolean ws = new AtomicBoolean(false);

    //@Bean
    //@RefreshScope
    public RouteLocator gateway(RouteLocatorBuilder rlb) {
        var id = "customers";
        //if (!this.ws.get()) {
            this.ws.set(true);
            return rlb.routes()
                    .route(id, routeSpec -> {
                        return routeSpec.path("/customers").uri("lb://customers/");
                    }).build();
       // }

//        return rlb.routes()
//                .route(id, routeSpec -> {
//                    return routeSpec
//                            .path("/customers")
//                            .filters(gatewayFilterSpec -> {
//                               return gatewayFilterSpec.setPath("/websocket");
//                            })
//                            .uri("lb://customers/");
//                }).build();

    }

    @Bean
    public ApplicationListener<RefreshRoutesResultEvent> routesRefresh() {

        // for reloading config
        // curl -XPOST http://localhost:9999/actuator/refresh => reloading configuration with this curl
        return new ApplicationListener<RefreshRoutesResultEvent>() {
            @Override
            public void onApplicationEvent(RefreshRoutesResultEvent event) {
                System.out.println("routes updated");
                var crl = (CachingRouteLocator) event.getSource();
                Flux<Route> routes = crl.getRoutes();
/*                routes.subscribe(route -> {
                    System.out.println(route);
                });*/

                routes.subscribe(System.out::println);
            }
        };

    }

}
