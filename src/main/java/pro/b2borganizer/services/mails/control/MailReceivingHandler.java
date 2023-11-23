package pro.b2borganizer.services.mails.control;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MailReceivingHandler {

    private final MimeMessageHandler mimeMessageHandler;

    @ServiceActivator(inputChannel = "imapChannel")
    public void handleMessage(Message<?> message) {
        if (message.getPayload() instanceof MimeMessage mimeMessage) {
            mimeMessageHandler.handle(mimeMessage);
        } else {
            log.warn("Message payload is not instance of MimeMessage!");
        }
    }

    @ServiceActivator(inputChannel = "errorChannel")
    public void handleErrorMessage(Message<?> message) {
        log.error("Error happened = {}.", message);
    }
}

