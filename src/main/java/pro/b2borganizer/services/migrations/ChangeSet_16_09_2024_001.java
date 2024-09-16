package pro.b2borganizer.services.migrations;

import java.util.List;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pro.b2borganizer.services.documents.control.DocumentPreviewsGenerator;
import pro.b2borganizer.services.documents.control.ManagedDocumentRepository;
import pro.b2borganizer.services.documents.entity.UnableToGeneratePreviewsException;
import pro.b2borganizer.services.files.entity.ManagedFile;

@ChangeUnit(id = "changeset-16_09_2024_1", order = "001", author = "piotr@raszkowski.pl")
@Slf4j
@RequiredArgsConstructor
public class ChangeSet_16_09_2024_001 {
    private final ManagedDocumentRepository managedDocumentRepository;

    private final DocumentPreviewsGenerator documentPreviewsGenerator;

    @Execution
    public void changeSet() {
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

    @RollbackExecution
    public void rollback() {
    }
}
