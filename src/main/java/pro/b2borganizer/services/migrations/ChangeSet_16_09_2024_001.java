package pro.b2borganizer.services.migrations;

import java.util.List;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import pro.b2borganizer.services.documents.control.DocumentPreviewsGenerator;
import pro.b2borganizer.services.documents.control.ManagedDocumentPreviewsGenerator;
import pro.b2borganizer.services.documents.control.ManagedDocumentRepository;
import pro.b2borganizer.services.documents.entity.ManagedDocumentPreviewsGenerationEvent;
import pro.b2borganizer.services.documents.entity.UnableToGeneratePreviewsException;
import pro.b2borganizer.services.files.entity.ManagedFile;

@ChangeUnit(id = "changeset-16_09_2024_1", order = "001", author = "piotr@raszkowski.pl")
@Slf4j
@RequiredArgsConstructor
public class ChangeSet_16_09_2024_001 {

    private final ManagedDocumentPreviewsGenerator managedDocumentPreviewsGenerator;

    @Execution
    public void changeSet() {
        managedDocumentPreviewsGenerator.generate(ManagedDocumentPreviewsGenerationEvent.builder().type(ManagedDocumentPreviewsGenerationEvent.Type.ALL).build());
    }

    @RollbackExecution
    public void rollback() {
    }
}
