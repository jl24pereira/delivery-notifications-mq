package com.jlpereira.mq_shipment_sender.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlpereira.mq_shipment_sender.model.dto.ShipmentRequestDTO;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Queue;
import jakarta.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MessageSenderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSenderService.class);
    private final JmsTemplate jmsTemplate;
    private final Queue requestQueue;
    private final Queue responseQueue; // Cola de respuesta
    private final ObjectMapper objectMapper; // Para convertir el DTO a JSON

    public MessageSenderService(JmsTemplate jmsTemplate, Queue requestQueue, Queue responseQueue, ObjectMapper objectMapper) {
        this.jmsTemplate = jmsTemplate;
        this.requestQueue = requestQueue;
        this.responseQueue = responseQueue;
        this.objectMapper = objectMapper;
    }

    /**
     * Sends the shipment message as a JSON to the request queue and waits for a response.
     *
     * @param shipmentRequest The shipment request DTO containing shipment details
     * @return true if the message was successfully sent and response received, false otherwise
     */
    public boolean sendShipmentMessage(ShipmentRequestDTO shipmentRequest) {
        try {
            // Convert ShipmentRequestDTO to JSON
            String messageContent = convertShipmentToJson(shipmentRequest);

            // Generate a unique JMSCorrelationID for the message
            String correlationId = UUID.randomUUID().toString();

            LOGGER.info("Sending shipment message for orderId: {} with correlationId: {}", shipmentRequest.orderId(), correlationId);

            // Send the message to the request queue with the correlation ID
            jmsTemplate.send(requestQueue, session -> {
                TextMessage message = session.createTextMessage(messageContent);
                message.setJMSCorrelationID(correlationId); // Set the correlation ID
                return message;
            });

            // Wait for the response in the response queue with the same correlation ID
            LOGGER.info("Waiting for response in response queue for correlationId: {}", correlationId);
            /*Message responseMessage = jmsTemplate.receiveSelected(responseQueue, "JMSCorrelationID='" + correlationId + "'");

            if (responseMessage instanceof TextMessage textMessage) {
                String responseText = textMessage.getText();
                LOGGER.info("Received response for orderId: {} with correlationId: {}: {}", shipmentRequest.orderId(), correlationId, responseText);
                return true;
            } else {
                LOGGER.error("No valid response received for correlationId: {}", correlationId);
                return false;
            }*/

        } catch (JsonProcessingException e) {
            LOGGER.error("Error sending shipment message for orderId: {}. Error: {}", shipmentRequest.orderId(), e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Converts the ShipmentRequestDTO into a JSON string using ObjectMapper.
     *
     * @param shipmentRequest The shipment request DTO
     * @return A string representation of the shipment request in JSON format
     * @throws JsonProcessingException if the conversion to JSON fails
     */
    private String convertShipmentToJson(ShipmentRequestDTO shipmentRequest) throws JsonProcessingException {
        return objectMapper.writeValueAsString(shipmentRequest);
    }
}
