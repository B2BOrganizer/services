package pro.b2borganizer.services.documents.boundary;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.b2borganizer.services.documents.control.DocumentCategorizationService;

@RestController
@RequestMapping("/managed-documents-categorizations")
@RequiredArgsConstructor
@Slf4j
public class ManagedDocumentsCategorizationsResource {

    private final DocumentCategorizationService documentCategorizationService;

    /**
     * Categorizes document(s) using AI.
     * If documentId is provided, categorizes that specific document.
     * If documentId is null or empty, categorizes all uncategorized documents.
     *
     * @param request categorization request with optional documentId
     * @return categorization result
     */
    @PostMapping
    public ResponseEntity<?> categorize(@RequestBody(required = false) CategorizationRequest request) {
        log.info("Received categorization request: {}", request);

        if (request == null || StringUtils.isBlank(request.documentId())) {
            // Categorize all uncategorized documents
            log.info("Categorizing all uncategorized documents");
            DocumentCategorizationService.CategorizationSummary summary =
                    documentCategorizationService.categorizeAll();

            return ResponseEntity.ok(new CategorizationResponse(
                    true,
                    String.format("Categorized %d out of %d documents",
                            summary.successful(), summary.total()),
                    summary
            ));
        } else {
            // Categorize specific document
            log.info("Categorizing document: {}", request.documentId());
            try {
                DocumentCategorizationService.CategorizationResult result =
                        documentCategorizationService.categorizeSingle(request.documentId());

                return ResponseEntity.ok(new CategorizationResponse(
                        result.success(),
                        result.message(),
                        result
                ));
            } catch (IllegalArgumentException e) {
                log.error("Document not found: {}", request.documentId(), e);
                return ResponseEntity.badRequest()
                        .body(new CategorizationResponse(false, e.getMessage(), null));
            } catch (Exception e) {
                log.error("Error categorizing document: {}", request.documentId(), e);
                return ResponseEntity.internalServerError()
                        .body(new CategorizationResponse(false, "Internal error: " + e.getMessage(), null));
            }
        }
    }

    // Request/Response records
    public record CategorizationRequest(String documentId) {}
    public record CategorizationResponse(boolean success, String message, Object data) {}
}
