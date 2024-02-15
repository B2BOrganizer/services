package pro.b2borganizer.services.files.boundary;

import java.nio.file.Files;
import java.text.MessageFormat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import pro.b2borganizer.services.documents.control.ManagedDocumentRepository;

@RestController
@RequestMapping("/managed-files")
@RequiredArgsConstructor
@Slf4j
public class ManagedFilesResource {

    private final ManagedDocumentRepository managedDocumentRepository;

    @GetMapping(value = "/{managedDocumentId}")
    public ResponseEntity<Resource> getManagedFile(@PathVariable(value = "managedDocumentId") String managedDocumentId) {
        log.info("Get managed file for document = {}", managedDocumentId);

        return managedDocumentRepository.findById(managedDocumentId).map(managedDocument -> {
                    byte[] fileBytes = Base64.decodeBase64(managedDocument.getManagedFile().getContentInBase64());

                    Resource resource = new ByteArrayResource(fileBytes);

                    return ResponseEntity.ok()
                            .header("Content-Disposition", "attachment; filename=" + managedDocument.getManagedFile().getFileName())
//                            .contentLength(file.length())
                            .contentType(MediaType.parseMediaType("application/json+simpleRestProvider"))
                            .body(resource);

//                    return ResponseEntity.ok()
//                            .header("Content-Disposition", "attachment; filename=" + managedDocument.getManagedFile().getFileName())
//                            .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                            .body((StreamingResponseBody) outputStream -> outputStream.write(fileBytes));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MessageFormat.format("Managed document with id = {0} not found.", managedDocumentId)));
    }
}
