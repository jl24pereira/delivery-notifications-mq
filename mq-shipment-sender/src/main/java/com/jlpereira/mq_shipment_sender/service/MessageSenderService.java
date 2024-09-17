package com.jlpereira.mq_shipment_sender.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlpereira.mq_shipment_sender.model.dto.ShipmentRequestDTO;
import com.jlpereira.mq_shipment_sender.model.dto.ShipmentResponseDTO;
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
    private final Queue responseQueue;
    private final ObjectMapper objectMapper;

    public MessageSenderService(JmsTemplate jmsTemplate, Queue requestQueue, Queue responseQueue, ObjectMapper objectMapper) {
        this.jmsTemplate = jmsTemplate;
        this.requestQueue = requestQueue;
        this.responseQueue = responseQueue;
        this.objectMapper = objectMapper;
    }

    /**
     * Sends the shipment message and waits for a response.
     *
     * @param shipmentRequest The shipment request DTO.
     * @return A ShipmentResponseDTO indicating the result of the shipment request.
     */
    public ShipmentResponseDTO sendShipmentMessage(ShipmentRequestDTO shipmentRequest) {
        try {
            String messageContent = convertShipmentToJson(shipmentRequest);
            String correlationId = UUID.randomUUID().toString();

            sendMessage(messageContent, correlationId);
            Message responseMessage = waitForResponse(correlationId);

            return processResponse(responseMessage, shipmentRequest, correlationId);
        } catch (JsonProcessingException | JMSException e) {
            LOGGER.error("Error sending shipment message for orderId: {}. Error: {}", shipmentRequest.orderId(), e.getMessage());
            return new ShipmentResponseDTO(shipmentRequest.orderId(), "FAILED", "Error processing shipment: " + e.getMessage());
        }
    }

    /**
     * Sends the message to the request queue.
     *
     * @param messageContent The JSON content of the shipment request.
     * @param correlationId  The correlation ID.
     */
    private void sendMessage(String messageContent, String correlationId) {
        LOGGER.info("Sending shipment message with correlationId: {}", correlationId);

        jmsTemplate.send(requestQueue, session -> {
            TextMessage message = session.createTextMessage(messageContent);
            message.setJMSCorrelationID(correlationId);
            return message;
        });
    }

    /**
     * Waits for a response from the response queue.
     *
     * @param correlationId The correlation ID to filter the response.
     * @return The JMS response message.
     */
    private Message waitForResponse(String correlationId) {
        LOGGER.info("Waiting for response with correlationId: {}", correlationId);
        return jmsTemplate.receiveSelected(responseQueue, "JMSCorrelationID='" + correlationId + "'");
    }

    /**
     * Processes the response message.
     *
     * @param responseMessage The JMS response message.
     * @param shipmentRequest The original shipment request DTO.
     * @param correlationId   The correlation ID.
     * @return A ShipmentResponseDTO based on the response.
     * @throws JMSException If there's an error processing the response.
     * @throws JsonProcessingException If the response can't be parsed to a DTO.
     */
    private ShipmentResponseDTO processResponse(Message responseMessage, ShipmentRequestDTO shipmentRequest, String correlationId)
            throws JMSException, JsonProcessingException {

        if (responseMessage instanceof TextMessage textMessage) {
            String responseText = textMessage.getText();
            LOGGER.info("Received response for orderId: {} with correlationId: {}", shipmentRequest.orderId(), correlationId);
            return objectMapper.readValue(responseText, ShipmentResponseDTO.class);
        } else {
            LOGGER.error("No valid response received for correlationId: {}", correlationId);
            return new ShipmentResponseDTO(shipmentRequest.orderId(), "FAILED", "No response received");
        }
    }

    /**
     * Converts the ShipmentRequestDTO into a JSON string.
     *
     * @param shipmentRequest The shipment request DTO.
     * @return A JSON string representing the shipment request.
     * @throws JsonProcessingException If the conversion to JSON fails.
     */
    private String convertShipmentToJson(ShipmentRequestDTO shipmentRequest) throws JsonProcessingException {
        return objectMapper.writeValueAsString(shipmentRequest);
    }
}
