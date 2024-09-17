package com.jlpereira.mq_shipment_processor.model.dto;

import java.time.LocalDate;

/**
 * DTO representing the shipment message details.
 *
 * @param orderId        The ID of the order.
 * @param customerEmail  The email of the customer.
 * @param trackingNumber The tracking number for the shipment.
 * @param shippingDate   The date the shipment was sent.
 */
public record ShipmentMessageDTO(
        String orderId,
        String customerEmail,
        String trackingNumber,
        LocalDate shippingDate
) {}
