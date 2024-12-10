package pro.b2borganizer.services.documents.control;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pro.b2borganizer.services.documents.entity.ManagedDocumentPreviewsGenerationEvent;
import pro.b2borganizer.services.documents.entity.UnableToGeneratePreviewsException;
import pro.b2borganizer.services.files.entity.ManagedFile;
import pro.b2borganizer.services.mails.entity.MailReceivedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class ManagedDocumentPreviewsGenerator {

    private final ManagedDocumentRepository managedDocumentRepository;

    private final DocumentPreviewsGenerator documentPreviewsGenerator;

    @EventListener
    @Async
    public void generate(ManagedDocumentPreviewsGenerationEvent managedDocumentPreviewsGenerationEvent) {
        if (managedDocumentPreviewsGenerationEvent.getType() == ManagedDocumentPreviewsGenerationEvent.Type.ALL) {
            managedDocumentRepository.findAll().forEach(managedDocument -> {
                try {
                    log.info("Generating previews for managed document = {}.", managedDocument);
                    List<ManagedFile> previews = documentPreviewsGenerator.generatePreviews(managedDocument.getManagedFile());

                    managedDocument.setManagedFilePreviews(previews);

                    managedDocumentRepository.save(managedDocument);
                } catch (UnableToGeneratePreviewsException e) {
                    log.error("Unable to generate previews for managed document = {}.", managedDocument, e);
                }
            });
        }
    }
}
