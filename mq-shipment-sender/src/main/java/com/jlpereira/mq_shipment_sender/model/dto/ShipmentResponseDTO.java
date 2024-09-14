package com.jlpereira.mq_shipment_sender.model.dto;

import java.time.LocalDate;

public record ShipmentResponseDTO(
        String orderId,
        String status,
        String message
) {
}
