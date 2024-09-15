package com.jlpereira.mq_shipment_procesor.model.dto;

public record ShipmentResponseDTO(
        String orderId,
        String status,
        String message
) {
}
