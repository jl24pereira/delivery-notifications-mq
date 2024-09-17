package com.jlpereira.mq_shipment_processor.model.dto;

/**
 * DTO representing the response to a shipment request.
 *
 * @param orderId The ID of the order.
 * @param status  The status of the shipment (e.g., success or failure).
 * @param message A message describing the status.
 */
public record ShipmentResponseDTO(
        String orderId,
        String status,
        String message
) {
}
