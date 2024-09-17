package com.jlpereira.mq_shipment_processor.config;

import com.ibm.mq.jakarta.jms.MQConnectionFactory;
import com.ibm.mq.jakarta.jms.MQQueue;
import com.ibm.msg.client.wmq.common.CommonConstants;
import jakarta.jms.JMSException;
import jakarta.jms.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

/**
 * Configuration for IBM MQ and JMS messaging.
 */
@Configuration
@EnableJms
public class ConfigMQ {

    @Value("${ibm.mq.host}")
    private String host;

    @Value("${ibm.mq.port}")
    private Integer port;

    @Value("${ibm.mq.queueManager}")
    private String queueManager;

    @Value("${ibm.mq.channel}")
    private String channel;

    @Value("${ibm.mq.queue.response}")
    private String responseQueue;

    /**
     * Configures the IBM MQ connection factory.
     *
     * @return Configured MQConnectionFactory.
     * @throws JMSException if any error occurs during setup.
     */
    @Bean
    public MQConnectionFactory mqConnectionFactory() throws JMSException {
        MQConnectionFactory factory = new MQConnectionFactory();
        factory.setHostName(host);
        factory.setPort(port);
        factory.setQueueManager(queueManager);
        factory.setChannel(channel);
        factory.setIntProperty(CommonConstants.WMQ_CONNECTION_MODE, CommonConstants.WMQ_CM_CLIENT);
        return factory;
    }

    /**
     * Configures the JMS listener container factory.
     *
     * @param mqConnectionFactory The MQ connection factory.
     * @return Configured DefaultJmsListenerContainerFactory.
     */
    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(MQConnectionFactory mqConnectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(mqConnectionFactory);
        return factory;
    }

    /**
     * Configures the JMS template for sending messages.
     *
     * @param mqConnectionFactory The MQ connection factory.
     * @return Configured JmsTemplate.
     */
    @Bean
    public JmsTemplate jmsTemplate(MQConnectionFactory mqConnectionFactory) {
        return new JmsTemplate(mqConnectionFactory);
    }

    /**
     * Configures the response queue.
     *
     * @return Configured Queue.
     * @throws JMSException if any error occurs.
     */
    @Bean
    public Queue responseQueue() throws JMSException {
        return new MQQueue(responseQueue);
    }
}
