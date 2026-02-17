package pro.b2borganizer.services.documents.boundary;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.b2borganizer.services.documents.control.DocumentEmbeddingService;

@RestController
@RequestMapping("/managed-documents-embeddings")
@RequiredArgsConstructor
@Slf4j
public class ManagedDocumentsEmbeddingsResource {

    private final DocumentEmbeddingService documentEmbeddingService;

    /**
     * Rebuilds embeddings for all categorized documents.
     * This creates/updates the knowledge base used for AI categorization.
     *
     * @return result of rebuild operation
     */
    @PostMapping
    public ResponseEntity<RebuildEmbeddingsResponse> rebuildEmbeddings() {
        log.info("Received request to rebuild embeddings");

        try {
            int embeddingsCreated = documentEmbeddingService.rebuildAllEmbeddings();

            return ResponseEntity.ok(new RebuildEmbeddingsResponse(
                    true,
                    String.format("Successfully rebuilt %d embeddings", embeddingsCreated),
                    embeddingsCreated
            ));
        } catch (Exception e) {
            log.error("Error rebuilding embeddings", e);
            return ResponseEntity.internalServerError()
                    .body(new RebuildEmbeddingsResponse(
                            false,
                            "Error rebuilding embeddings: " + e.getMessage(),
                            0
                    ));
        }
    }

    // Response record
    public record RebuildEmbeddingsResponse(boolean success, String message, int embeddingsCreated) {}
}
