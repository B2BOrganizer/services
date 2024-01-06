package pro.b2borganizer.services.mails.control;

import java.text.MessageFormat;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pro.b2borganizer.services.errors.boundary.MailProcessingErrorReporter;
import pro.b2borganizer.services.mails.entity.MailMessage;
import pro.b2borganizer.services.mails.entity.MailMessageError;
import pro.b2borganizer.services.mails.entity.MailParserException;
import pro.b2borganizer.services.mails.entity.MailReceivedEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailMessageHandler {
    private final MimeMessageParser mimeMessageParser;

    private final MailMessageRepository mailMessageRepository;

    private final MailMessageErrorRepository mailMessageErrorRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final MailProcessingErrorReporter mailProcessingErrorReporter;

    public void handle(MimeMessage mimeMessage) {
        try {
            MailMessage mailMessage = mimeMessageParser.parse(mimeMessage);

            log.info("Received mail message: {}", mailMessage);

            MailMessage savedMessage = mailMessageRepository.save(mailMessage);

            if (mailMessage.hasErrors()) {
                mailProcessingErrorReporter.reportErrors(mailMessage);
            }

            applicationEventPublisher.publishEvent(MailReceivedEvent.builder().mailMessageId(savedMessage.getId()).build());
        } catch (MailParserException e) {
            handleError(mimeMessage, e);
        }
    }

    private void handleError(MimeMessage mimeMessage, MailParserException e) {
        try {
            log.error("Unable to parse mime message, unknown error. Mime message is probably set as SEEN or flagged on the server and won't be processed again.", e);
            MailMessageError mailMessageError = mimeMessageParser.parse(mimeMessage, e);
            mailMessageErrorRepository.save(mailMessageError);
            mailProcessingErrorReporter.reportException(mailMessageError.getId(), "Unable to parse mime message, unknown error. Mime message is probably set as SEEN or flagged on the server and won't be processed again.", e);
        } catch (MailParserException ex) {
            log.error("Unable to handle mime message parsing error, something unexpected happened.", e);
            throw new RuntimeException("Unable to handle mime message parsing error, something unexpected happened.", e);
        }
    }
}
