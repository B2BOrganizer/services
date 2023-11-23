package pro.b2borganizer.services.mails.control;

import org.springframework.integration.annotation.MessagingGateway;
import pro.b2borganizer.services.mails.entity.MailToSend;

@MessagingGateway(defaultRequestChannel = "mailChannel")
public interface MailGateway {
    void sendMail(MailToSend mailToSend);
}
