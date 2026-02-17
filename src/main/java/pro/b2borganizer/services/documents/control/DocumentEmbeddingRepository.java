package pro.b2borganizer.services.documents.control;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pro.b2borganizer.services.documents.entity.DocumentEmbedding;

@Repository
public interface DocumentEmbeddingRepository extends MongoRepository<DocumentEmbedding, String> {

    Optional<DocumentEmbedding> findByManagedDocumentId(String managedDocumentId);

    List<DocumentEmbedding> findByRequiredDocumentIdNotNull();

    boolean existsByManagedDocumentId(String managedDocumentId);

    void deleteByManagedDocumentId(String managedDocumentId);
}
