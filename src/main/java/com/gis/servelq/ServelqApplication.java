package com.gis.servelq;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.gis.servelq.models")
@EnableJpaRepositories(basePackages = "com.gis.servelq.repository")
public class ServelqApplication implements ApplicationListener<WebServerInitializedEvent> {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        // Set system properties for Spring Boot
        setSystemProperty("TWILIO_SID", dotenv);
        setSystemProperty("TWILIO_TOKEN", dotenv);
        setSystemProperty("TWILIO_FROM", dotenv);

        SpringApplication.run(ServelqApplication.class, args);
    }

    private static void setSystemProperty(String key, Dotenv dotenv) {
        String value = dotenv.get(key);
        if (value != null) {
            System.setProperty(key, value);
        }
    }

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        int port = event.getWebServer().getPort();
        System.out.println("Server Started At PORT: " + port);
    }
}
