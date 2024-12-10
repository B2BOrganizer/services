package pro.b2borganizer.services.timesheets.control;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pro.b2borganizer.services.timesheets.entity.WorkLog;

@Repository
public interface WorkLogsRepository extends MongoRepository<WorkLog, String> {
    Optional<WorkLog> findOptionalByDay(LocalDate day);
}
