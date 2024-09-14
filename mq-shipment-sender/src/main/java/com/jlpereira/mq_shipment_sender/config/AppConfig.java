package com.jlpereira.mq_shipment_sender.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
