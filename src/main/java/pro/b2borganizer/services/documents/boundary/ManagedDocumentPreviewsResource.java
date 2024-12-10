package pro.b2borganizer.services.documents.boundary;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pro.b2borganizer.services.documents.control.ManagedDocumentRepository;
import pro.b2borganizer.services.documents.entity.ManagedDocument;
import pro.b2borganizer.services.documents.entity.ManagedDocumentPreviews;

@RestController
@RequestMapping("/managed-documents-previews")
@RequiredArgsConstructor
@Slf4j
public class ManagedDocumentPreviewsResource {

    private final ManagedDocumentRepository managedDocumentRepository;

    @GetMapping("/{id}")
    public ManagedDocumentPreviews get(@PathVariable(value = "id") String id) {
        log.info("Get managed document preview = {}", id);

        ManagedDocument managedDocument = managedDocumentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MessageFormat.format("Managed document with id = {0} not found.", id)));

        List<String> previews = managedDocument.getManagedFilePreviews().stream()
                .map(p -> p.getMimeType() + ";base64," + p.getContentInBase64())
                .toList();

        ManagedDocumentPreviews managedDocumentPreviews = new ManagedDocumentPreviews();
        managedDocumentPreviews.setId(managedDocument.getId());
        managedDocumentPreviews.setPreviews(previews);

        return managedDocumentPreviews;
    }
}
