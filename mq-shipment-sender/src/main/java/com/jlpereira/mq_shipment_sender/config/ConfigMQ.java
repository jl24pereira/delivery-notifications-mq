package com.jlpereira.mq_shipment_sender.config;

import com.ibm.mq.jakarta.jms.MQConnectionFactory;
import com.ibm.mq.jakarta.jms.MQQueue;
import com.ibm.msg.client.wmq.common.CommonConstants;
import jakarta.jms.JMSException;
import jakarta.jms.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

/**
 * Configuration class for setting up IBM MQ connection and JMS messaging.
 * This class defines the necessary beans to establish a connection with IBM MQ,
 * create a JMS listener container, and provide a JmsTemplate for sending messages.
 */
@Configuration
public class ConfigMQ {

    @Value("${ibm.mq.host}")
    private String host;

    @Value("${ibm.mq.port}")
    private Integer port;

    @Value("${ibm.mq.queueManager}")
    private String queueManager;

    @Value("${ibm.mq.channel}")
    private String channel;

    @Value("${ibm.mq.queue.request}")
    private String requestQueue;

    @Value("${ibm.mq.queue.response}")
    private String responseQueue;

    /**
     * Creates and configures the MQConnectionFactory bean for IBM MQ.
     * The connection factory is responsible for establishing connections to the IBM MQ server.
     * It uses the provided host, port, queue manager, and channel properties to configure the connection.
     *
     * @return MQConnectionFactory configured with the necessary IBM MQ properties
     * @throws Exception if there is an error during the creation of the connection factory
     */
    @Bean
    public MQConnectionFactory mqConnectionFactory() throws JMSException {
        MQConnectionFactory factory = new MQConnectionFactory();

        factory.setHostName(host);
        factory.setPort(port);
        factory.setQueueManager(queueManager);
        factory.setChannel(channel);

        // Set the connection mode to client
        factory.setIntProperty(CommonConstants.WMQ_CONNECTION_MODE, CommonConstants.WMQ_CM_CLIENT);
        return factory;
    }


    /**
     * Creates and configures the JmsTemplate bean for sending messages to IBM MQ.
     * The JmsTemplate simplifies sending and receiving messages to/from IBM MQ.
     *
     * @param mqConnectionFactory The MQConnectionFactory used to connect to IBM MQ
     * @return JmsTemplate configured to work with IBM MQ
     */
    @Bean
    public JmsTemplate jmsTemplate(MQConnectionFactory mqConnectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(mqConnectionFactory);
        jmsTemplate.setReceiveTimeout(5000);
        return jmsTemplate;
    }

    /**
     * Bean que representa la cola de solicitudes.
     *
     * @return La cola de solicitudes
     * @throws JMSException si hay algún problema al configurar la cola
     */
    @Bean
    public Queue requestQueue() throws JMSException {
        return new MQQueue(requestQueue);
    }

    /**
     * Bean que representa la cola de respuestas.
     *
     * @return La cola de respuestas
     * @throws JMSException si hay algún problema al configurar la cola
     */
    @Bean
    public Queue responseQueue() throws JMSException {
        return new MQQueue(responseQueue);
    }
}
