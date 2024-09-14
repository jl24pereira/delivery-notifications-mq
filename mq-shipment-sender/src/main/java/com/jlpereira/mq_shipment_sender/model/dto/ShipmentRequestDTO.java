package com.jlpereira.mq_shipment_sender.model.dto;

import java.time.LocalDate;

public record ShipmentRequestDTO(
        String orderId,
        String customerEmail,
        String trackingNumber,
        LocalDate shippingDate
) {
}
