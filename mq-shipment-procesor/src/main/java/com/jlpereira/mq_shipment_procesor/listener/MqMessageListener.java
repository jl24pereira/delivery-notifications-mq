package com.jlpereira.mq_shipment_procesor.listener;

import com.jlpereira.mq_shipment_procesor.commons.util.MessageConverter;
import com.jlpereira.mq_shipment_procesor.model.dto.ShipmentMessageDTO;
import com.jlpereira.mq_shipment_procesor.service.ShipmentService;
import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MqMessageListener {

    protected static final Logger LOG = LoggerFactory.getLogger(MqMessageListener.class);

    private final MessageConverter messageConverter;
    private final ShipmentService shipmentService;

    /**
     * Constructor for the MqMessageListener class.
     * Initializes the listener with a MessageConverter to convert incoming messages,
     * and a ShipmentService to process the shipment.
     *
     * @param messageConverter Utility to convert JSON messages to ShipmentMessageDTO
     * @param shipmentService  Service responsible for processing shipments and sending notifications
     */
    public MqMessageListener(MessageConverter messageConverter, ShipmentService shipmentService) {
        this.messageConverter = messageConverter;
        this.shipmentService = shipmentService;
    }

    /**
     * Listens to the specified queue for incoming shipment messages.
     * When a message is received, it extracts the message payload and the correlation ID,
     * converts the payload into a ShipmentMessageDTO, and passes it to the ShipmentService
     * for further processing, including sending an email and responding to the queue.
     *
     * @param message The TextMessage received from the queue, containing the shipment details as JSON
     * @throws JMSException If an error occurs during message processing
     */
    @JmsListener(destination = "${ibm.mq.queue.request}")
    public void receiveMessage(TextMessage message) throws JMSException {
        // Extract the correlation ID to associate the response with the original request
        String correlationId = message.getJMSCorrelationID();

        // Extract the message payload (expected to be JSON)
        String payload = message.getText();

        // Log the received message and correlation ID for tracking purposes
        LOG.info("Received message with Correlation ID: {}", correlationId);
        LOG.info("Message payload: {}", payload);

        // Convert the payload from JSON to a ShipmentMessageDTO using the MessageConverter
        ShipmentMessageDTO shipmentMessageDTO = messageConverter.fromMessage(payload);

        // Process the shipment using ShipmentService, passing the message details and correlation ID
        shipmentService.processShipment(shipmentMessageDTO, correlationId);
    }

}
