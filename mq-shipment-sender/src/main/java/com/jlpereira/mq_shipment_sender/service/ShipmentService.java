package com.jlpereira.mq_shipment_sender.service;

import com.jlpereira.mq_shipment_sender.model.dto.ShipmentRequestDTO;
import com.jlpereira.mq_shipment_sender.model.dto.ShipmentResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for processing shipment requests.
 */
@Service
public class ShipmentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShipmentService.class);
    private final MessageSenderService messageSenderService;

    /**
     * Constructor for initializing the ShipmentService.
     *
     * @param messageSenderService The service responsible for sending shipment messages.
     */
    public ShipmentService(MessageSenderService messageSenderService) {
        this.messageSenderService = messageSenderService;
    }

    /**
     * Processes a shipment request, sends the shipment details to the request queue,
     * and waits for a response in the response queue.
     *
     * @param shipmentRequest The shipment request DTO containing shipment details.
     * @return A ShipmentResponseDTO indicating success or failure.
     */
    public ShipmentResponseDTO processShipment(ShipmentRequestDTO shipmentRequest) {
        return messageSenderService.sendShipmentMessage(shipmentRequest);
    }
}
