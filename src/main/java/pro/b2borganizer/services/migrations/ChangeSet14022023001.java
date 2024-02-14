package pro.b2borganizer.services.migrations;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pro.b2borganizer.services.documents.control.ManagedDocumentRepository;

@ChangeUnit(id = "changeset-14022023", order = "001", author = "piotr@raszkowski.pl")
@Slf4j
@RequiredArgsConstructor
public class ChangeSet14022023001 {
    private final ManagedDocumentRepository managedDocumentRepository;

    @Execution
    public void changeSet() {
        managedDocumentRepository.findAll().forEach(managedDocument -> {
            if (managedDocument.getAssignedToYear() == null) {
                managedDocument.setAssignedToYear(managedDocument.getReceived().getYear());
            }

            if (managedDocument.getAssignedToMonth() == null) {
                managedDocument.setAssignedToMonth(managedDocument.getReceived().getMonthValue());
            }

            if (!managedDocument.getSent().toLocalDate().isEqual(managedDocument.getReceived().toLocalDate())) {
                managedDocument.setReceived(managedDocument.getSent());
            }

            managedDocumentRepository.save(managedDocument);
        });
    }

    @RollbackExecution
    public void rollback() {
    }
}
