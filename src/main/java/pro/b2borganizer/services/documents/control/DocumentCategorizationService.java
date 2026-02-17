package pro.b2borganizer.services.documents.control;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.b2borganizer.services.documents.entity.DocumentEmbedding;
import pro.b2borganizer.services.documents.entity.ManagedDocument;
import pro.b2borganizer.services.documents.entity.RequiredDocumentSelectionType;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentCategorizationService {

    private static final int TOP_K_SIMILAR_DOCUMENTS = 5;
    private static final double MIN_CONFIDENCE_THRESHOLD = 0.60; // 60%

    private final DocumentEmbeddingService documentEmbeddingService;
    private final DocumentEmbeddingRepository documentEmbeddingRepository;
    private final ManagedDocumentRepository managedDocumentRepository;

    /**
     * Categorizes a single document using similarity search with embeddings.
     *
     * @param documentId the ID of the document to categorize
     * @return the categorization result
     */
    public CategorizationResult categorizeSingle(String documentId) {
        log.info("Starting categorization for document: {}", documentId);

        ManagedDocument document = managedDocumentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));

        // Generate embedding for this document if it doesn't exist
        DocumentEmbedding documentEmbedding = documentEmbeddingRepository
                .findByManagedDocumentId(documentId)
                .orElseGet(() -> documentEmbeddingService.generateAndStoreEmbedding(document));

        if (documentEmbedding == null || documentEmbedding.getEmbedding() == null) {
            log.error("Failed to generate embedding for document: {}", documentId);
            return new CategorizationResult(false, "Failed to generate embedding", null, 0.0);
        }

        // Get all embeddings from categorized documents (knowledge base)
        List<DocumentEmbedding> knowledgeBase = documentEmbeddingRepository.findByRequiredDocumentIdNotNull();

        if (knowledgeBase.isEmpty()) {
            log.warn("No categorized documents in knowledge base. Please run rebuild embeddings first.");
            return new CategorizationResult(false, "No knowledge base available", null, 0.0);
        }

        // Calculate similarity scores
        List<SimilarDocument> similarDocuments = knowledgeBase.stream()
                .filter(embedding -> !embedding.getManagedDocumentId().equals(documentId)) // Exclude self
                .map(embedding -> {
                    double similarity = cosineSimilarity(
                            documentEmbedding.getEmbedding(),
                            embedding.getEmbedding()
                    );
                    return new SimilarDocument(embedding.getRequiredDocumentId(), similarity);
                })
                .sorted(Comparator.comparingDouble(SimilarDocument::similarity).reversed())
                .limit(TOP_K_SIMILAR_DOCUMENTS)
                .toList();

        if (similarDocuments.isEmpty()) {
            return new CategorizationResult(false, "No similar documents found", null, 0.0);
        }

        // Vote on category based on top K similar documents
        Map<String, Long> voteCounts = similarDocuments.stream()
                .collect(Collectors.groupingBy(SimilarDocument::categoryId, Collectors.counting()));

        // Find the category with most votes
        Map.Entry<String, Long> winningCategory = voteCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        if (winningCategory == null) {
            return new CategorizationResult(false, "No category could be determined", null, 0.0);
        }

        // Calculate confidence (percentage of votes for winning category)
        double confidence = (double) winningCategory.getValue() / TOP_K_SIMILAR_DOCUMENTS;

        log.info("Document {} categorized as {} with confidence {}",
                documentId, winningCategory.getKey(), confidence);

        // Only apply categorization if confidence is above threshold
        if (confidence >= MIN_CONFIDENCE_THRESHOLD) {
            document.setRequiredDocumentId(winningCategory.getKey());
            document.setRequiredDocumentSelectionType(RequiredDocumentSelectionType.AI);
            document.setAiConfidence(confidence);
            managedDocumentRepository.save(document);

            // Update embedding with new category
            documentEmbedding.setRequiredDocumentId(winningCategory.getKey());
            documentEmbeddingRepository.save(documentEmbedding);

            return new CategorizationResult(true, "Categorized successfully",
                    winningCategory.getKey(), confidence);
        } else {
            log.info("Confidence {} below threshold {} for document {}",
                    confidence, MIN_CONFIDENCE_THRESHOLD, documentId);
            return new CategorizationResult(false,
                    "Confidence below threshold: " + String.format("%.2f", confidence),
                    winningCategory.getKey(), confidence);
        }
    }

    /**
     * Categorizes all uncategorized documents.
     *
     * @return summary of categorization results
     */
    public CategorizationSummary categorizeAll() {
        log.info("Starting categorization for all uncategorized documents");

        List<ManagedDocument> uncategorizedDocuments = managedDocumentRepository.findAll().stream()
                .filter(doc -> StringUtils.isBlank(doc.getRequiredDocumentId()))
                .toList();

        log.info("Found {} uncategorized documents", uncategorizedDocuments.size());

        int successCount = 0;
        int failedCount = 0;

        for (ManagedDocument document : uncategorizedDocuments) {
            try {
                CategorizationResult result = categorizeSingle(document.getId());
                if (result.success()) {
                    successCount++;
                } else {
                    failedCount++;
                }
            } catch (Exception e) {
                log.error("Failed to categorize document: {}", document.getId(), e);
                failedCount++;
            }
        }

        log.info("Categorization completed: {} successful, {} failed out of {} total",
                successCount, failedCount, uncategorizedDocuments.size());

        return new CategorizationSummary(uncategorizedDocuments.size(), successCount, failedCount);
    }

    /**
     * Calculates cosine similarity between two vectors.
     * Returns a value between -1 and 1, where 1 means identical direction.
     *
     * @param vectorA first vector
     * @param vectorB second vector
     * @return cosine similarity score
     */
    private double cosineSimilarity(List<Double> vectorA, List<Double> vectorB) {
        if (vectorA == null || vectorB == null || vectorA.size() != vectorB.size()) {
            throw new IllegalArgumentException("Vectors must be non-null and same size");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.size(); i++) {
            dotProduct += vectorA.get(i) * vectorB.get(i);
            normA += Math.pow(vectorA.get(i), 2);
            normB += Math.pow(vectorB.get(i), 2);
        }

        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    // Result records
    public record CategorizationResult(boolean success, String message, String categoryId, double confidence) {}
    public record CategorizationSummary(int total, int successful, int failed) {}
    private record SimilarDocument(String categoryId, double similarity) {}
}
