package com.jlpereira.mq_shipment_sender.service;

import com.jlpereira.mq_shipment_sender.model.dto.ShipmentRequestDTO;
import com.jlpereira.mq_shipment_sender.model.dto.ShipmentResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ShipmentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShipmentService.class);
    private final MessageSenderService messageSenderService;

    public ShipmentService(MessageSenderService messageSenderService) {
        this.messageSenderService = messageSenderService;
    }

    /**
     * Processes a shipment request, sends the shipment details to the request queue,
     * and waits for a response in the response queue.
     *
     * @param shipmentRequest The shipment request DTO containing shipment details
     * @return ShipmentResponseDTO indicating success or failure
     */
    public ShipmentResponseDTO processShipment(ShipmentRequestDTO shipmentRequest) {
        try {
            LOGGER.info("Processing shipment for orderId: {}", shipmentRequest.orderId());

            // Delegate to MessageSenderService to send the message to the request queue
            boolean isSent = messageSenderService.sendShipmentMessage(shipmentRequest);

            if (isSent) {
                LOGGER.info("Shipment for orderId {} processed successfully", shipmentRequest.orderId());
                return new ShipmentResponseDTO(shipmentRequest.orderId(), "SUCCESS", "Shipment processed successfully");
            } else {
                LOGGER.error("Failed to process shipment for orderId {}", shipmentRequest.orderId());
                return new ShipmentResponseDTO(shipmentRequest.orderId(), "FAILED", "Failed to process shipment");
            }
        } catch (Exception e) {
            LOGGER.error("Error processing shipment for orderId: {}. Error: {}", shipmentRequest.orderId(), e.getMessage());
            return new ShipmentResponseDTO(shipmentRequest.orderId(), "FAILED", "Error processing shipment: " + e.getMessage());
        }
    }
}
