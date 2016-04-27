package org.mongoops.client.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * This service provides alerting capabilities.
 */
@Service
public class EmailAlertService {

    private static final Log log = LogFactory.getLog(EmailAlertService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Autowired
    private MessageSource messageSource;

    @Value("${mongodb.replicaset.alert.email.toEmail:}")
    private String alertToEmail;
    @Value("${mongodb.replicaset.alert.email.fromEmail:mongoops@mongodb.service}")
    private String alertFromEmail;
    @Value("${mongodb.replicaset.alert.replyToEmail:no_reply@mongodb.service}")
    private String alertReplyToEmail;

    public void sendMessage(String subjectKey, String bodyKey, Object... args) {

        try {
            Locale locale = Locale.getDefault();
            String subject = this.messageSource.getMessage(subjectKey, args, locale);
            String body = this.messageSource.getMessage(bodyKey, args, locale);

            if (this.mailSender != null &&
                this.alertToEmail != null &&
                !this.alertToEmail.isEmpty()) {

                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(this.alertToEmail);
                mailMessage.setReplyTo(this.alertFromEmail);
                mailMessage.setFrom(this.alertReplyToEmail);

                mailMessage.setSubject(subject);
                mailMessage.setText(body);

                this.mailSender.send(mailMessage);

            } else {
                log.warn(
                    String.format("Alert Email Not Sent as email sender has not " +
                        "been configured =>\nSubject: %s\nBody: %s", subject, body));
            }
        } catch (NoSuchMessageException e) {

            log.error(
                String.format("No message found for given code: %s",
                e.getLocalizedMessage()), e);
        }
    }
}
