package pro.b2borganizer.services.reports.control;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pro.b2borganizer.services.documents.entity.ManagedDocument;
import pro.b2borganizer.services.reports.entity.ManagedDocumentsReport;
import pro.b2borganizer.services.reports.entity.PredefinedMailMonthlyReport;

@Repository
public interface ManagedDocumentsReportRepository extends MongoRepository<ManagedDocumentsReport, String> {
    Optional<ManagedDocumentsReport> findOptionalById(String id);
}
