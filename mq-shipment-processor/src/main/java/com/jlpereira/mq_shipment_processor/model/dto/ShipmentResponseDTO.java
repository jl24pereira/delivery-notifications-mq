package com.jlpereira.mq_shipment_processor.model.dto;

public record ShipmentResponseDTO(
        String orderId,
        String status,
        String message
) {
}
