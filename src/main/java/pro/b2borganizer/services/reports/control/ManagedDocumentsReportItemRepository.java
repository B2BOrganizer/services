package pro.b2borganizer.services.reports.control;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pro.b2borganizer.services.reports.entity.ManagedDocumentsReport;
import pro.b2borganizer.services.reports.entity.ManagedDocumentsReportItem;

@Repository
public interface ManagedDocumentsReportItemRepository extends MongoRepository<ManagedDocumentsReportItem, String> {

    void deleteByManagedDocumentsReportId(String managedDocumentsReportId);
}
