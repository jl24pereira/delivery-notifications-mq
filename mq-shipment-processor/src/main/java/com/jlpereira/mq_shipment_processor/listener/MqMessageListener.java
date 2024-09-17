package com.jlpereira.mq_shipment_processor.listener;

import com.jlpereira.mq_shipment_processor.commons.util.MessageConverter;
import com.jlpereira.mq_shipment_processor.model.dto.ShipmentMessageDTO;
import com.jlpereira.mq_shipment_processor.service.ShipmentService;
import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Listener for receiving messages from the IBM MQ queue.
 */
@Component
public class MqMessageListener {

    protected static final Logger LOG = LoggerFactory.getLogger(MqMessageListener.class);

    private final MessageConverter messageConverter;
    private final ShipmentService shipmentService;

    /**
     * Constructor for initializing the listener with a message converter and shipment service.
     *
     * @param messageConverter Utility to convert messages.
     * @param shipmentService  Service for processing shipments.
     */
    public MqMessageListener(MessageConverter messageConverter, ShipmentService shipmentService) {
        this.messageConverter = messageConverter;
        this.shipmentService = shipmentService;
    }

    /**
     * Receives and processes incoming messages from the queue.
     *
     * @param message The message received from the queue.
     * @throws JMSException If message processing fails.
     */
    @JmsListener(destination = "${ibm.mq.queue.request}")
    public void receiveMessage(TextMessage message) throws JMSException {
        String correlationId = message.getJMSCorrelationID();
        String payload = message.getText();

        LOG.info("Received message with Correlation ID: {}", correlationId);
        LOG.info("Message payload: {}", payload);

        ShipmentMessageDTO shipmentMessageDTO = messageConverter.fromMessage(payload);
        shipmentService.processShipment(shipmentMessageDTO, correlationId);
    }
}
