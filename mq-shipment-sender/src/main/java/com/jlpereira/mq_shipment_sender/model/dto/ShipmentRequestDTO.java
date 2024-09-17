package com.jlpereira.mq_shipment_sender.model.dto;

import java.time.LocalDate;

/**
 * DTO representing the shipment request details.
 *
 * @param orderId        The ID of the order.
 * @param customerEmail  The customer's email address.
 * @param trackingNumber The tracking number for the shipment.
 * @param shippingDate   The date the shipment was made.
 */
public record ShipmentRequestDTO(
        String orderId,
        String customerEmail,
        String trackingNumber,
        LocalDate shippingDate
) {
}
