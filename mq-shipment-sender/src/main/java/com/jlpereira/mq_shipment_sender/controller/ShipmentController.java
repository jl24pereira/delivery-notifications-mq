package com.jlpereira.mq_shipment_sender.controller;

import com.jlpereira.mq_shipment_sender.model.dto.ShipmentRequestDTO;
import com.jlpereira.mq_shipment_sender.model.dto.ShipmentResponseDTO;
import com.jlpereira.mq_shipment_sender.service.ShipmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling shipment requests.
 */
@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShipmentController.class);
    private final ShipmentService shipmentService;

    /**
     * Constructor for initializing the ShipmentController.
     *
     * @param shipmentService The service responsible for processing shipments.
     */
    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    /**
     * Endpoint to process a shipment request.
     *
     * @param shipmentRequest The shipment request DTO containing shipment details.
     * @return ResponseEntity containing the shipment response (success or failure).
     */
    @PostMapping
    public ResponseEntity<ShipmentResponseDTO> createShipment(@RequestBody ShipmentRequestDTO shipmentRequest) {
        try {
            LOGGER.info("Received shipment request for orderId: {}", shipmentRequest.orderId());

            ShipmentResponseDTO response = shipmentService.processShipment(shipmentRequest);

            if ("SUCCESS".equalsIgnoreCase(response.status())) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            LOGGER.error("Error processing shipment for orderId: {}", shipmentRequest.orderId(), e);
            return new ResponseEntity<>(new ShipmentResponseDTO(
                    shipmentRequest.orderId(), "FAILED", "Internal server error: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
