package pro.b2borganizer.services.mails.control;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import pro.b2borganizer.services.mails.entity.MailToSend;

@Component
@Slf4j
@RequiredArgsConstructor
public class MailSendingHandler {

    private final JavaMailSender javaMailSender;
    @ServiceActivator(inputChannel = "mailOutputChannel")
    public void handle(Message<MailToSend> message) {
        try {
            MailToSend mailToSend = message.getPayload();

            log.info("Sending mail: {}", mailToSend);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            mimeMessageHelper.setTo(mailToSend.getTo());
            if (mailToSend.getCc() != null) {
                mimeMessageHelper.setCc(mailToSend.getCc());
            }
            mimeMessageHelper.setSubject(mailToSend.getSubject());
            mimeMessageHelper.setText(mailToSend.getContent(), true);
            mailToSend.getAttachments().forEach(managedFile -> {
                try {
                    mimeMessageHelper.addAttachment(managedFile.getFileName(), new ByteArrayDataSource(Base64.decodeBase64(managedFile.getContentInBase64()), managedFile.getMimeType()));
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            });

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
