package com.jlpereira.mq_shipment_sender.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for creating beans.
 */
@Configuration
public class AppConfig {

    /**
     * Provides a configured {@link ObjectMapper} bean for JSON serialization/deserialization.
     *
     * @return Configured {@link ObjectMapper} instance.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
