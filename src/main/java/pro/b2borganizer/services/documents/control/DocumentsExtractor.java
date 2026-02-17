package pro.b2borganizer.services.documents.control;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


import javax.imageio.ImageIO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pro.b2borganizer.services.documents.entity.ManagedDocument;
import pro.b2borganizer.services.errors.boundary.MailProcessingErrorReporter;
import pro.b2borganizer.services.files.entity.ManagedFile;
import pro.b2borganizer.services.mails.control.MailMessageRepository;
import pro.b2borganizer.services.mails.entity.MailReceivedEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class DocumentsExtractor {

    private final MailMessageRepository mailMessageRepository;

    private final ManagedDocumentRepository managedDocumentRepository;

    private final MailProcessingErrorReporter mailProcessingErrorReporter;

    private final MailContentParser mailContentParser;

    @Value("#{'${pro.b2borganizer.allowedManagedDocumentExtensions}'.split(',')}")
    private Set<String> allowedManagedDocumentExceptions;

    private final DocumentPreviewsGenerator documentPreviewsGenerator;

    @EventListener
    public void extractDocuments(MailReceivedEvent mailReceivedEvent) {
        log.info("Extracting documents for = {}.", mailReceivedEvent);

        try {
            mailMessageRepository.findById(mailReceivedEvent.getMailMessageId())
                    .ifPresent(mailMessage -> mailMessage.getMailAttachments().stream().map(mailAttachment -> {
                        ManagedDocument managedDocument = new ManagedDocument();
                        managedDocument.setManagedFile(mailAttachment.getManagedFile());
                        managedDocument.setMailMessageId(mailMessage.getId());
                        managedDocument.setReceived(mailMessage.getReceived());
                        managedDocument.setSent(mailMessage.getSent());

                        MailContentParser.AssignedYearMonth assignedYearMonth = mailContentParser.parseMailContent(mailMessage.getContent()).orElse(mailMessage.getAssignedYearMonth());

                        managedDocument.setAssignedToYear(assignedYearMonth.year().getValue());
                        managedDocument.setAssignedToMonth(assignedYearMonth.month().getValue());

                        try {
                            List<ManagedFile> previews = documentPreviewsGenerator.generatePreviews(managedDocument.getManagedFile());
                            managedDocument.setManagedFilePreviews(previews);
                        } catch (Exception e) {
                            log.info("Unable to generate previews for managed document = {}.", managedDocument, e);
                        }

                        return managedDocument;
                    }).filter(managedDocument -> allowedManagedDocumentExceptions.contains(FilenameUtils.getExtension(managedDocument.getManagedFile().getFileName()).toLowerCase())).forEach(managedDocument -> {
                        log.info("Document extracted = {}.", managedDocument);

                        managedDocumentRepository.save(managedDocument);
                    }));
        } catch (Exception e) {
            log.error("Unable to extract documents from {}.", mailReceivedEvent, e);
            mailProcessingErrorReporter.reportException(mailReceivedEvent.getMailMessageId(), MessageFormat.format("Unable to extract documents from {0}.", mailReceivedEvent.getMailMessageId()), e);
        }
    }
}
