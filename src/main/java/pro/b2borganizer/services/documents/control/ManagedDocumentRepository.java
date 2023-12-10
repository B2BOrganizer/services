package pro.b2borganizer.services.documents.control;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pro.b2borganizer.services.documents.entity.ManagedDocument;

@Repository
public interface ManagedDocumentRepository extends MongoRepository<ManagedDocument, String> {

    List<ManagedDocument> findByReceivedBetween(LocalDate from, LocalDate to);

    Optional<ManagedDocument> findOptionalById(String id);
}
