package com.jlpereira.mq_shipment_processor.commons.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlpereira.mq_shipment_processor.model.dto.ShipmentMessageDTO;
import jakarta.jms.JMSException;
import org.springframework.stereotype.Component;

/**
 * Converts JSON messages into {@link ShipmentMessageDTO} objects.
 */
@Component
public class MessageConverter {

    private final ObjectMapper objectMapper;

    /**
     * Initializes the converter with a configured {@link ObjectMapper}.
     *
     * @param objectMapper The Jackson {@link ObjectMapper} used for deserialization.
     */
    public MessageConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Converts a JSON string to a {@link ShipmentMessageDTO}.
     *
     * @param jsonMessage The JSON string containing shipment data.
     * @return The corresponding {@link ShipmentMessageDTO}.
     * @throws JMSException If deserialization fails.
     */
    public ShipmentMessageDTO fromMessage(String jsonMessage) throws JMSException {
        try {
            return objectMapper.readValue(jsonMessage, ShipmentMessageDTO.class);
        } catch (JsonProcessingException e) {
            throw new JMSException("Failed to convert JSON to ShipmentMessageDTO: " + e.getMessage());
        }
    }
}
