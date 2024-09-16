package com.jlpereira.mq_shipment_processor.model.dto;

import java.time.LocalDate;

public record ShipmentMessageDTO(
        String orderId,
        String customerEmail,
        String trackingNumber,
        LocalDate shippingDate
) {}
