package pro.b2borganizer.services.errors.boundary;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.util.StackTraceUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import pro.b2borganizer.services.mails.control.MailGateway;
import pro.b2borganizer.services.mails.entity.MailToSend;
import pro.b2borganizer.services.templates.boundary.TemplateParser;
import pro.b2borganizer.services.templates.control.TemplateRepository;
import pro.b2borganizer.services.templates.entity.Template;

@Component
@RequiredArgsConstructor
@Slf4j
public class MailProcessingErrorReporter {

    private final TemplateParser templateParser;

    private final MailGateway mailGateway;

    @Value("${pro.b2borganizer.mail.errorDestinationMailAddress}")
    private String errorDestinationMailAddress;

    public void reportError(MimeMessage mimeMessage, String message) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("mailMessageId", mimeMessage.getMessageID());
            variables.put("message", message);
//            variables.put("content", mimeMessage.getContent());

            report(message, variables);
        } catch (Exception e) {
            log.error("Fatal error while reporting error", e);
            reportException("UNKNOWN", "Fatal error while reporting error!", e);
        }
    }

    private void report(String message, Map<String, Object> variables) {
        String mailContent = templateParser.parseByCode("ERROR_REPORT", variables);

        MailToSend mailToSend = new MailToSend();
        mailToSend.setTo(errorDestinationMailAddress);
        mailToSend.setSubject(MessageFormat.format("[B2B Organizer]: {0}", message));
        mailToSend.setContent(mailContent);

        mailGateway.sendMail(mailToSend);
    }

    public void reportException(String mailMessageId, String message, Exception exception) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("mailMessageId", mailMessageId);
        variables.put("message", message);
        variables.put("stacktrace", ExceptionUtils.getStackTrace(exception));

        report(message, variables);
    }
}
