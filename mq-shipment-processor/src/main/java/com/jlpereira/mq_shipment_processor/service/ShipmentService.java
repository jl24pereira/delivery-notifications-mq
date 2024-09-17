package com.jlpereira.mq_shipment_processor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlpereira.mq_shipment_processor.model.dto.ShipmentMessageDTO;
import com.jlpereira.mq_shipment_processor.model.dto.ShipmentResponseDTO;
import jakarta.jms.Queue;
import jakarta.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

/**
 * Service for processing shipment messages and sending notifications.
 */
@Service
public class ShipmentService {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ShipmentService.class);

    private final NotificationService notificationService;
    private final JmsTemplate jmsTemplate;
    private final Queue responseQueue;
    private final ObjectMapper objectMapper;

    /**
     * Constructor to initialize the shipment service.
     *
     * @param notificationService The service for sending notifications.
     * @param jmsTemplate         The JMS template for interacting with the message queue.
     * @param responseQueue       The queue for sending response messages.
     * @param objectMapper        The object mapper for serializing JSON.
     */
    public ShipmentService(NotificationService notificationService, JmsTemplate jmsTemplate, Queue responseQueue, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.jmsTemplate = jmsTemplate;
        this.responseQueue = responseQueue;
        this.objectMapper = objectMapper;
    }

    /**
     * Processes the shipment message and sends a notification to the customer.
     * Responds to the queue with success or failure of the notification.
     *
     * @param shipmentMessageDTO The shipment details.
     * @param correlationId      The correlation ID for tracking the response.
     */
    public void processShipment(ShipmentMessageDTO shipmentMessageDTO, String correlationId) {
        boolean emailSent = sendNotification(shipmentMessageDTO);
        ShipmentResponseDTO responseDTO = new ShipmentResponseDTO(
                shipmentMessageDTO.orderId(),
                emailSent ? "SUCCESS" : "FAILED",
                emailSent ? "Email sent successfully" : "Email sending failed"
        );
        sendResponseMessage(correlationId, responseDTO);
    }

    /**
     * Sends a notification email to the customer with shipment details.
     *
     * @param shipmentMessageDTO The shipment details.
     * @return true if the email was sent successfully, false otherwise.
     */
    private boolean sendNotification(ShipmentMessageDTO shipmentMessageDTO) {
        String emailBody = "Dear customer,\n\n" +
                "Your order with ID: " +
                shipmentMessageDTO.orderId() +
                " has been shipped.\n" +
                "Tracking Number: " +
                shipmentMessageDTO.trackingNumber() +
                "\n" +
                "Shipping Date: " +
                shipmentMessageDTO.shippingDate() +
                "\n\n" +
                "Thank you for shopping with us.\n\n" +
                "Best regards,\n" +
                "The Shipping Team";

        return notificationService.sendEmail(shipmentMessageDTO.customerEmail(), "Shipment Confirmation", emailBody);
    }

    /**
     * Sends a response message to the queue indicating the result of the notification.
     *
     * @param correlationId The correlation ID for the response.
     * @param responseDTO   The response DTO.
     */
    private void sendResponseMessage(String correlationId, ShipmentResponseDTO responseDTO) {
        try {
            String responseJson = objectMapper.writeValueAsString(responseDTO);

            jmsTemplate.send(responseQueue, session -> {
                TextMessage response = session.createTextMessage(responseJson);
                response.setJMSCorrelationID(correlationId);
                return response;
            });

            LOGGER.info("Sent response message: {} with Correlation ID: {}", responseJson, correlationId);

        } catch (JsonProcessingException e) {
            LOGGER.error("Error sending response for orderId: {}", responseDTO.orderId(), e);
        }
    }
}
