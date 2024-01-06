package pro.b2borganizer.services.mails.control;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.ZoneId;

import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import pro.b2borganizer.services.errors.boundary.MailProcessingErrorReporter;
import pro.b2borganizer.services.mails.entity.MailAttachment;
import pro.b2borganizer.services.mails.entity.MailMessage;
import pro.b2borganizer.services.files.entity.ManagedFile;
import pro.b2borganizer.services.mails.entity.MailMessageError;
import pro.b2borganizer.services.mails.entity.MailParseError;
import pro.b2borganizer.services.mails.entity.MailParserException;
import pro.b2borganizer.services.mails.entity.UnknownMultipartException;

@Slf4j
@Component
@RequiredArgsConstructor
public class MimeMessageParser {

    private final MailProcessingErrorReporter mailProcessingErrorReporter;

    /**
     * https://i.stack.imgur.com/JlQ40.png
     *
     * multipart/mixed
     *     multipart/alternative
     *       text/plain - a plain text version of the main message body
     *       multipart/related
     *         text/html - the html version of the main message body
     *         image/jpeg - an image referenced by the main body
     *     application/octet-stream (or whatever) - the attachment
     *
     * @param mimeMessage
     * @return
     */
    public MailMessage parse(MimeMessage mimeMessage) throws MailParserException, UnknownMultipartException {
        try {
            log.info("Parsing mime message with id = {}, flags = {}.", mimeMessage.getMessageID(), mimeMessage.getFlags());

            MailMessage mailMessage = new MailMessage();
            mailMessage.setSerializedMimeMessage(serializeMimeMessageToBase64(mimeMessage));
            mailMessage.setSender(mimeMessage.getFrom()[0].toString());
            mailMessage.setSubject(mimeMessage.getSubject());
            mailMessage.setReceived(mimeMessage.getReceivedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            mailMessage.setSent(mimeMessage.getSentDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

            if (mimeMessage.isMimeType("multipart/*")) {
                log.info("Mail is multipart = {}.", mimeMessage.getContentType());

                if (mimeMessage.isMimeType("multipart/mixed")) {
                    log.info("Mail is multipart/mixed = {}.", mimeMessage.getContentType());

                    parseMultipartMixed(mimeMessage, mailMessage);
                } else if (mimeMessage.isMimeType("multipart/alternative")) {
                    log.info("Mail is multipart/alternative = {}.", mimeMessage.getContentType());

                    parseMultipartAlternative(mimeMessage, mailMessage);
                } else {
                    log.warn("Unknown multipart = {}!", mimeMessage.getContentType());
                    MailParseError mailParseError = new MailParseError();
                    mailParseError.setDescription(MessageFormat.format("Unknown multipart = {0}!", mimeMessage.getContentType()));

                    mailMessage.addMailParseError(mailParseError);

                    throw new UnknownMultipartException(mailMessage, mimeMessage.getContentType());
                }
            } else {
                log.info("Mail is not multipart = {}.", mimeMessage.getContentType());
                mailMessage.setPlainTextContent((String) mimeMessage.getContent());
            }

            return mailMessage;
        } catch (MessagingException | IOException e) {
            log.error("Error while parsing mime message!", e);
            throw new MailParserException(e);
        }
    }

    private String serializeMimeMessageToBase64(MimeMessage mimeMessage) throws MessagingException, IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        mimeMessage.writeTo(byteArrayOutputStream);

        return Base64.encodeBase64String(byteArrayOutputStream.toByteArray());
    }

    private void parseMultipartMixed(Part part, MailMessage mailMessage) throws MessagingException, IOException {
        MimeMultipart mimeMultipart = (MimeMultipart) part.getContent();

        for (int i = 0; i < mimeMultipart.getCount(); i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);

            if (bodyPart.isMimeType("multipart/*")) {
                if (bodyPart.isMimeType("multipart/alternative")) {
                    log.info("Mime multipart/alternative found inside multipart/mixed = {}.", bodyPart.getContentType());

                    parseMultipartAlternative(bodyPart, mailMessage);
                } else if (bodyPart.isMimeType("multipart/related")) {
                    log.info("Mime multipart/related found inside multipart/mixed = {}.", bodyPart.getContentType());

                    parseMultipartRelated(bodyPart, mailMessage);
                } else {
                    log.warn("Unknown multipart = {} found inside multipart/mixed!", bodyPart.getContentType());
                    MailParseError mailParseError = new MailParseError();
                    mailParseError.setDescription(MessageFormat.format("Unknown part = {0} found for multipart/mixed content with disposition = {}!", bodyPart.getContentType(), bodyPart.getDisposition()));

                    mailMessage.addMailParseError(mailParseError);

                }
            } else if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                log.info("Mail attachment found fit filename = {}, content type = {}.", bodyPart.getFileName(), bodyPart.getContentType());
                MailAttachment mailAttachment = buildMailAttachment(bodyPart);
                mailMessage.addMailAttachment(mailAttachment);
            } else {
                log.warn("Unknown part = {} found for multipart/mixed content with disposition = {}!", bodyPart.getContentType(), bodyPart.getDisposition());

                MailParseError mailParseError = new MailParseError();
                mailParseError.setDescription(MessageFormat.format("Unknown part = {0} found for multipart/mixed content with disposition = {}!", bodyPart.getContentType(), bodyPart.getDisposition()));

                mailMessage.addMailParseError(mailParseError);
            }
        }
    }

    private MailAttachment buildMailAttachment(BodyPart bodyPart) throws MessagingException, IOException {
        ManagedFile managedFile = new ManagedFile();
        managedFile.setFileName(bodyPart.getFileName());
        managedFile.setMimeType(bodyPart.getContentType());
        managedFile.setContentInBase64(Base64.encodeBase64String(IOUtils.toByteArray(bodyPart.getInputStream())));

        MailAttachment mailAttachment = new MailAttachment();
        mailAttachment.setManagedFile(managedFile);
        mailAttachment.setDisposition(bodyPart.getDisposition());

        return mailAttachment;
    }

    private void parseMultipartAlternative(Part part, MailMessage mailMessage) throws MessagingException, IOException {
        MimeMultipart mimeMultipart = (MimeMultipart) part.getContent();

        for (int i = 0; i < mimeMultipart.getCount(); i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);

            if (bodyPart.isMimeType("multipart/*")) {
                log.info("Mime multipart found inside multipart/alternative = {}.", bodyPart.getContentType());

                if (bodyPart.isMimeType("multipart/related")) {
                    parseMultipartRelated(bodyPart, mailMessage);
                } else {
                    MailParseError mailParseError = new MailParseError();
                    mailParseError.setDescription(MessageFormat.format("Unexpected multipart = {0} found inside multipart/alternative content!", bodyPart.getContentType()));

                    mailMessage.addMailParseError(mailParseError);
                }
            } else if (bodyPart.isMimeType("text/plain")) {
                log.info("Plain text found inside multipart/alternative = {}.", bodyPart.getContentType());

                mailMessage.setPlainTextContent((String) bodyPart.getContent());
            } else if (bodyPart.isMimeType("text/html")) {
                log.info("Html text found inside multipart/alternative = {}.", bodyPart.getContentType());

                mailMessage.setHtmlContent((String) bodyPart.getContent());
            } else {
                MailParseError mailParseError = new MailParseError();
                mailParseError.setDescription(MessageFormat.format("Unexpected content type = {0} found inside multipart/alternative content!", bodyPart.getContentType()));

                mailMessage.addMailParseError(mailParseError);
            }
        }
    }

    private void parseMultipartRelated(Part part, MailMessage mailMessage) throws MessagingException, IOException {
        MimeMultipart mimeMultipart = (MimeMultipart) part.getContent();

        for (int i = 0; i < mimeMultipart.getCount(); i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);

            if (bodyPart.isMimeType("image/*")) {
                log.info("Image found inside multipart/related = {} with disposition = {}.", bodyPart.getContentType(), bodyPart.getDisposition());

                MailAttachment mailAttachment = buildMailAttachment(bodyPart);
                mailMessage.addContentRelatedMailAttachment(mailAttachment);
            } else if (bodyPart.isMimeType("multipart/*")) {
                log.info("Mime multipart found inside multipart/related = {}.", bodyPart.getContentType());

                if (bodyPart.isMimeType("multipart/alternative")) {
                    log.info("Mime multipart/alternative found inside multipart/related = {}.", bodyPart.getContentType());
                    parseMultipartAlternative(bodyPart, mailMessage);
                } else {
                    MailParseError mailParseError = new MailParseError();
                    mailParseError.setDescription(MessageFormat.format("Unexpected multipart = {0} found inside multipart/related content!", bodyPart.getContentType()));
                    mailMessage.addMailParseError(mailParseError);
                }
            } else {
                MailParseError mailParseError = new MailParseError();
                mailParseError.setDescription(MessageFormat.format("Unexpected part = {0} found inside multipart/related content!", bodyPart.getContentType()));
                mailMessage.addMailParseError(mailParseError);
            }
        }
    }

    public MailMessageError parse(MimeMessage mimeMessage, Exception exception) throws MailParserException {
        try {
            MailMessageError mailMessageError = new MailMessageError();
            mailMessageError.setSerializedMimeMessage(serializeMimeMessageToBase64(mimeMessage));
            mailMessageError.setMessageId(mimeMessage.getMessageID());
            mailMessageError.setStackTrace(ExceptionUtils.getStackTrace(exception));
            return mailMessageError;
        } catch (MessagingException | IOException e) {
            throw new MailParserException(e);
        }
    }
}
