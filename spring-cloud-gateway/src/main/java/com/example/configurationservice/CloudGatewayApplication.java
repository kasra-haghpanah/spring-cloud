package com.example.configurationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@EnableDiscoveryClient
@SpringBootApplication
//@OpenAPIDefinition(info = @Info(title = "API Gateway", version = "v0.0.1", description = "Documentation API Gateway v0.0.1"))
public class CloudGatewayApplication {
    // https://medium.com/@oguz.topal/central-swagger-in-spring-cloud-gateway-697a1c37b03d
    public static void main(String[] args) {
        SpringApplication.run(CloudGatewayApplication.class, args);
    }

}
