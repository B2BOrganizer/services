package pro.b2borganizer.services.common.control;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pro.b2borganizer.services.reports.entity.MailMonthlyReport;
import pro.b2borganizer.services.reports.entity.MailMonthlyReportsFilter;

@Repository
@RequiredArgsConstructor
@Slf4j
public class SimpleRestProviderRepository {

    private final MongoTemplate mongoTemplate;

    public <R> SimpleRestProviderQueryListResult<R> findByQuery(SimpleRestProviderFilter<?> simpleRestProviderFilter, Class<R> entityClass) {
        log.info("Finding all {} for = {}.", entityClass, simpleRestProviderFilter);

        long totalElements = mongoTemplate.count(simpleRestProviderFilter.toCountQuery(), entityClass);

        List<R> result = mongoTemplate.find(simpleRestProviderFilter.toFilterQuery(), entityClass);

        return new SimpleRestProviderQueryListResult<>(totalElements, result);
    }

    public record SimpleRestProviderQueryListResult<R>(long totalElements, List<R> results) {
    }

    public <R> Optional<R> getOne(String id, Class<R> entityClass) {
        log.info("Getting one {} for id = {}.", entityClass, id);

        return Optional.ofNullable(mongoTemplate.findById(id, entityClass));
    }
}
