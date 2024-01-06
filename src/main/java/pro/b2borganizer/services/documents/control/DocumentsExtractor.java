package pro.b2borganizer.services.documents.control;

import java.text.MessageFormat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pro.b2borganizer.services.documents.entity.ManagedDocument;
import pro.b2borganizer.services.errors.boundary.MailProcessingErrorReporter;
import pro.b2borganizer.services.mails.control.MailMessageRepository;
import pro.b2borganizer.services.mails.entity.MailReceivedEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class DocumentsExtractor {

    private final MailMessageRepository mailMessageRepository;

    private final ManagedDocumentRepository managedDocumentRepository;

    private final MailProcessingErrorReporter mailProcessingErrorReporter;

    @EventListener
    public void extractDocuments(MailReceivedEvent mailReceivedEvent) {
        log.info("Extracting documents for = {}.", mailReceivedEvent);

        try {
            mailMessageRepository.findById(mailReceivedEvent.getMailMessageId())
                    .ifPresent(mailMessage -> mailMessage.getMailAttachments().stream().map(mailAttachment -> {
                        log.info("Extracting document = {}.", mailAttachment);

                        ManagedDocument managedDocument = new ManagedDocument();
                        managedDocument.setManagedFile(mailAttachment.getManagedFile());
                        managedDocument.setMailMessageId(mailMessage.getId());
                        managedDocument.setReceived(mailMessage.getReceived());
                        managedDocument.setSent(mailMessage.getSent());

                        return managedDocument;
                    }).forEach(managedDocument -> {
                        log.info("Document extracted = {}.", managedDocument);

                        managedDocumentRepository.save(managedDocument);
                    }));
        } catch (Exception e) {
            log.error("Unable to extract documents from {}.", mailReceivedEvent, e);
            mailProcessingErrorReporter.reportException(mailReceivedEvent.getMailMessageId(), MessageFormat.format("Unable to extract documents from {0}.", mailReceivedEvent.getMailMessageId()), e);
        }
    }
}
