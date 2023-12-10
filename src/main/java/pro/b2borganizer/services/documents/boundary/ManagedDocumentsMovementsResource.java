package pro.b2borganizer.services.documents.boundary;

import java.text.MessageFormat;
import java.time.LocalTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pro.b2borganizer.services.documents.control.ManagedDocumentRepository;
import pro.b2borganizer.services.documents.entity.ManagedDocument;
import pro.b2borganizer.services.documents.entity.NewManagedDocumentsMovement;
import pro.b2borganizer.services.documents.entity.UpdateManagedDocumentFields;

@RestController
@RequestMapping("/managed-documents-movements")
@RequiredArgsConstructor
@Slf4j
public class ManagedDocumentsMovementsResource {

    private final ManagedDocumentRepository managedDocumentRepository;

    @PostMapping
    public void update(@RequestBody NewManagedDocumentsMovement newManagedDocumentsMovement) {
        log.info("Moving managed documents = {}", newManagedDocumentsMovement);

        managedDocumentRepository.findByReceivedBetween(newManagedDocumentsMovement.getReceivedFrom(), newManagedDocumentsMovement.getReceivedTo())
                .forEach(managedDocument -> {
                    managedDocument.setReceived(newManagedDocumentsMovement.getNewReceived().atTime(LocalTime.now()));
                    managedDocumentRepository.save(managedDocument);
                });
    }
}
