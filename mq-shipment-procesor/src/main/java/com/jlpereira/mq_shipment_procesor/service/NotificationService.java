package com.jlpereira.mq_shipment_procesor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class NotificationService {

    protected static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

    private final Random random = new Random();

    /**
     * Simulates sending an email to a customer.
     * Logs the email details and randomly determines if the email was sent successfully.
     *
     * @param to      The recipient's email address
     * @param subject The subject of the email
     * @param body    The body content of the email
     * @return true if the email was successfully sent, false if there was a failure (simulated)
     */
    public boolean sendEmail(String to, String subject, String body) {
        LOG.info("Sending email with shipment details to: {}", to);
        LOG.info("Subject: {}", subject);
        LOG.info("Body: \n {}", body);

        boolean isSuccess = random.nextBoolean();

        if (isSuccess) {
            LOG.info("Email sent successfully.");
        } else {
            LOG.error("Email sending failed.");
        }

        return isSuccess;
    }
}
