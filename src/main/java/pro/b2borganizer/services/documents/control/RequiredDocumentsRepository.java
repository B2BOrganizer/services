package pro.b2borganizer.services.documents.control;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pro.b2borganizer.services.documents.entity.ManagedDocument;
import pro.b2borganizer.services.documents.entity.RequiredDocument;

@Repository
public interface RequiredDocumentsRepository extends MongoRepository<RequiredDocument, String> {
}
