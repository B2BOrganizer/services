package pro.b2borganizer.services.documents.control;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;
import pro.b2borganizer.services.documents.entity.DocumentEmbedding;
import pro.b2borganizer.services.documents.entity.ManagedDocument;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentEmbeddingService {

    private final EmbeddingModel embeddingModel;
    private final DocumentTextExtractor documentTextExtractor;
    private final DocumentEmbeddingRepository documentEmbeddingRepository;
    private final ManagedDocumentRepository managedDocumentRepository;

    /**
     * Generates and stores an embedding for a managed document.
     *
     * @param managedDocument the document to generate embedding for
     * @return the created DocumentEmbedding, or null if generation failed
     */
    public DocumentEmbedding generateAndStoreEmbedding(ManagedDocument managedDocument) {
        if (managedDocument == null || managedDocument.getManagedFile() == null) {
            log.warn("ManagedDocument or ManagedFile is null");
            return null;
        }

        log.info("Generating embedding for document: {}", managedDocument.getId());

        // Extract text from document
        String text = documentTextExtractor.extractText(managedDocument.getManagedFile());

        if (StringUtils.isBlank(text)) {
            log.warn("No text extracted from document: {}", managedDocument.getId());
            return null;
        }

        // Limit text length to avoid excessive processing (e.g., 10000 characters)
        String textToEmbed = text.length() > 10000 ? text.substring(0, 10000) : text;

        try {
            float[] output = embeddingModel.embed(textToEmbed);

            if (output == null || output.length == 0) {
                log.error("Failed to generate embedding for document: {}", managedDocument.getId());
                return null;
            }

            List<Double> embeddingVector = new ArrayList<>(output.length);
            for (float value : output) {
                embeddingVector.add((double) value);
            }


            DocumentEmbedding documentEmbedding = new DocumentEmbedding();
            documentEmbedding.setManagedDocumentId(managedDocument.getId());
            documentEmbedding.setEmbedding(embeddingVector);
            documentEmbedding.setTextContent(textToEmbed);
            documentEmbedding.setRequiredDocumentId(managedDocument.getRequiredDocumentId());
            documentEmbedding.setCreatedAt(LocalDateTime.now());

            documentEmbedding = documentEmbeddingRepository.save(documentEmbedding);

            log.info("Successfully generated and stored embedding for document: {}", managedDocument.getId());
            return documentEmbedding;

        } catch (Exception e) {
            log.error("Error generating embedding for document: {}", managedDocument.getId(), e);
            return null;
        }
    }

    /**
     * Rebuilds embeddings for all categorized documents (where requiredDocumentId is not null).
     * This creates the knowledge base for similarity search.
     *
     * @return number of embeddings successfully created
     */
    public int rebuildAllEmbeddings() {
        log.info("Starting to rebuild embeddings for all categorized documents");

        // Delete all existing embeddings
        documentEmbeddingRepository.deleteAll();

        // Find all documents with a category (requiredDocumentId != null)
        List<ManagedDocument> categorizedDocuments = managedDocumentRepository.findAll().stream()
                .filter(doc -> doc.getRequiredDocumentId() != null)
                .toList();

        log.info("Found {} categorized documents to process", categorizedDocuments.size());

        int successCount = 0;
        for (ManagedDocument document : categorizedDocuments) {
            try {
                DocumentEmbedding embedding = generateAndStoreEmbedding(document);
                if (embedding != null) {
                    successCount++;
                }
            } catch (Exception e) {
                log.error("Failed to generate embedding for document: {}", document.getId(), e);
            }
        }

        log.info("Successfully rebuilt {} embeddings out of {} categorized documents",
                successCount, categorizedDocuments.size());

        return successCount;
    }

    /**
     * Updates the embedding for a specific document.
     * Deletes existing embedding if present and creates a new one.
     *
     * @param managedDocumentId the ID of the document
     * @return the updated DocumentEmbedding, or null if failed
     */
    public DocumentEmbedding updateEmbedding(String managedDocumentId) {
        log.info("Updating embedding for document: {}", managedDocumentId);

        // Delete existing embedding
        documentEmbeddingRepository.deleteByManagedDocumentId(managedDocumentId);

        // Generate new embedding
        return managedDocumentRepository.findById(managedDocumentId)
                .map(this::generateAndStoreEmbedding)
                .orElse(null);
    }
}
