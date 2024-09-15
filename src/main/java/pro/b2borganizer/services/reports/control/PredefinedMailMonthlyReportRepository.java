package pro.b2borganizer.services.reports.control;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pro.b2borganizer.services.reports.entity.PredefinedMailMonthlyReport;

@Repository
public interface PredefinedMailMonthlyReportRepository extends MongoRepository<PredefinedMailMonthlyReport, String> {
}
