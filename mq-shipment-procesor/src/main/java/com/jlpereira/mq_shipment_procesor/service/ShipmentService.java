package com.jlpereira.mq_shipment_procesor.service;

import com.jlpereira.mq_shipment_procesor.model.dto.ShipmentMessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShipmentService {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ShipmentService.class);

    private final NotificationService notificationService;
    private final JmsTemplate jmsTemplate;

    /**
     * Constructor to initialize the shipment service.
     *
     * @param notificationService The service responsible for sending notifications (e.g., email)
     * @param jmsTemplate         The JMS template for interacting with the message queue
     */
    public ShipmentService(NotificationService notificationService, JmsTemplate jmsTemplate) {
        this.notificationService = notificationService;
        this.jmsTemplate = jmsTemplate;
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
        sendResponseMessage(correlationId, emailSent);
    }

    /**
     * Sends a notification email to the customer based on the shipment details provided.
     * The email contains the order ID, tracking number, and the shipping date.
     *
     * @param shipmentMessageDTO The DTO containing the shipment details (order ID, email, tracking number, etc.)
     * @return true if the email was sent successfully, false otherwise
     */
    private boolean sendNotification(ShipmentMessageDTO shipmentMessageDTO) {
        StringBuilder emailBody = new StringBuilder();
        emailBody.append("Dear customer,\n\n")
                .append("Your order with ID: ")
                .append(shipmentMessageDTO.orderId())
                .append(" has been shipped.\n")
                .append("Tracking Number: ")
                .append(shipmentMessageDTO.trackingNumber())
                .append("\n")
                .append("Shipping Date: ")
                .append(shipmentMessageDTO.shippingDate())
                .append("\n\n")
                .append("Thank you for shopping with us.\n\n")
                .append("Best regards,\n")
                .append("The Shipping Team");

        return notificationService.sendEmail(shipmentMessageDTO.customerEmail(), "Shipment Confirmation", emailBody.toString());
    }

    /**
     * Sends a response message back to the queue, indicating whether the email was successfully sent.
     * The response contains the same correlation ID as the original message, allowing the sender
     * to correlate the response with the original request.
     *
     * @param correlationId The correlation ID from the original message, to link the response to the request
     * @param success       A boolean indicating whether the email was sent successfully or not
     */
    private void sendResponseMessage(String correlationId, boolean success) {
        String responseMessage = success ? "Email sent successfully" : "Email sending failed";

        // Create and send response message with the same correlation ID
        /*jmsTemplate.send("shipping.notifications.response", session -> {
            TextMessage response = session.createTextMessage(responseMessage);
            response.setJMSCorrelationID(correlationId); // Set the same correlation ID
            return response;
        });*/

        LOGGER.info("Sent response message: {}  with Correlation ID: {}", responseMessage, correlationId);
    }
}
