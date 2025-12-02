package com.gis.servelq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.gis.servelq.models")
@EnableJpaRepositories(basePackages = "com.gis.servelq.repository")
public class ServelqApplication implements ApplicationListener<WebServerInitializedEvent> {

    public static void main(String[] args) {
        SpringApplication.run(ServelqApplication.class, args);
    }

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        int port = event.getWebServer().getPort();
        System.out.println("Server Started At PORT: " + port);
    }
}
