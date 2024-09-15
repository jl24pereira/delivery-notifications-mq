package com.jlpereira.mq_shipment_procesor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlpereira.mq_shipment_procesor.model.dto.ShipmentMessageDTO;
import com.jlpereira.mq_shipment_procesor.model.dto.ShipmentResponseDTO;
import jakarta.jms.JMSException;
import jakarta.jms.Queue;
import jakarta.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

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
     * @param notificationService The service responsible for sending notifications (e.g., email)
     * @param jmsTemplate         The JMS template for interacting with the message queue
     */
    public ShipmentService(NotificationService notificationService, JmsTemplate jmsTemplate, Queue responseQueue, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.jmsTemplate = jmsTemplate;
        this.responseQueue = responseQueue;
        this.objectMapper = objectMapper;
    }

    /**
     * Processes a shipment message received from the message queue.
     * It sends a notification to the customer and responds back to the response queue indicating
     * whether the notification was sent successfully or not.
     *
     * @param shipmentMessageDTO The shipment details to be processed (order ID, tracking number, etc.)
     * @param correlationId      The unique correlation ID used to associate the request and the response in the queue
     */
    public void processShipment(ShipmentMessageDTO shipmentMessageDTO, String correlationId) {
        boolean emailSent = sendNotification(shipmentMessageDTO);
        ShipmentResponseDTO responseDTO = new ShipmentResponseDTO(
                shipmentMessageDTO.orderId(),
                emailSent ? "SUCCESS":"FAILED",
                emailSent ? "Email sent successfully" : "Email sending failed"
        );
        sendResponseMessage(correlationId, responseDTO);
    }

    /**
     * Sends a notification email to the customer based on the shipment details provided.
     * The email contains the order ID, tracking number, and the shipping date.
     *
     * @param shipmentMessageDTO The DTO containing the shipment details (order ID, email, tracking number, etc.)
     * @return true if the email was sent successfully, false otherwise
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
     * Sends a response message to the response queue indicating success or failure of the email.
     *
     * @param correlationId The correlation ID to link request and response
     * @param responseDTO   The response DTO to send as JSON
     */
    private void sendResponseMessage(String correlationId, ShipmentResponseDTO responseDTO) {
        try {
            String responseJson = objectMapper.writeValueAsString(responseDTO);

            jmsTemplate.send(responseQueue, session -> {
                TextMessage response = session.createTextMessage(responseJson);
                response.setJMSCorrelationID(correlationId); // Set the same correlation ID
                return response;
            });

            LOGGER.info("Sent response message: {}  with Correlation ID: {}", responseJson, correlationId);

        } catch (JsonProcessingException e) {
            LOGGER.error("Error sending response for orderId: {}", responseDTO.orderId(), e);
        }

    }
}
