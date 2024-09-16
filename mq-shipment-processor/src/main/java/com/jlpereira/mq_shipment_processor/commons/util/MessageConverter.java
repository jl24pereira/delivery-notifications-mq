package com.jlpereira.mq_shipment_processor.commons.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jlpereira.mq_shipment_processor.model.dto.ShipmentMessageDTO;
import jakarta.jms.JMSException;
import org.springframework.stereotype.Component;

/**
 * Utility class for converting JSON messages into ShipmentMessageDTO objects.
 * This class uses Jackson's ObjectMapper to handle the deserialization of JSON
 * and supports Java 8 date/time types via the JavaTimeModule.
 */
@Component
public class MessageConverter {

    private final ObjectMapper objectMapper;

    /**
     * Constructor that initializes the MessageConverter with the provided ObjectMapper.
     *
     * @param objectMapper The ObjectMapper used for JSON deserialization
     */
    public MessageConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Converts a JSON string into a ShipmentMessageDTO object.
     * This method registers the JavaTimeModule with the ObjectMapper to handle
     * Java 8 date/time types (e.g., LocalDate) before performing the conversion.
     *
     * @param jsonMessage The JSON string representing the ShipmentMessageDTO
     * @return The converted ShipmentMessageDTO object
     * @throws JMSException If the JSON cannot be converted into a ShipmentMessageDTO
     */
    public ShipmentMessageDTO fromMessage(String jsonMessage) throws JMSException {
        try {
            // Register JavaTimeModule to handle LocalDate and other Java 8 time types
            objectMapper.registerModule(new JavaTimeModule());

            // Deserialize the JSON string into a ShipmentMessageDTO object
            return objectMapper.readValue(jsonMessage, ShipmentMessageDTO.class);
        } catch (JsonProcessingException e) {
            // Throw a JMSException if there is a failure during the conversion process
            throw new JMSException("Failed to convert JSON to ShipmentMessageDTO");
        }
    }
}
