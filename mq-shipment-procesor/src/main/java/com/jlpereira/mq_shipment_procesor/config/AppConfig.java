package com.jlpereira.mq_shipment_procesor.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    /**
     * Creates and configures a new ObjectMapper bean.
     * The ObjectMapper is used to convert between Java objects and JSON representations.
     * This bean will be available for injection across the entire application wherever it's required.
     *
     * @return A default instance of ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
