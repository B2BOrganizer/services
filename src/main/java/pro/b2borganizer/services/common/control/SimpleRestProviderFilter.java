package pro.b2borganizer.services.common.control;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Query;
import pro.b2borganizer.services.common.entity.CriteriaSupportable;

@Slf4j
public record SimpleRestProviderFilter<F extends CriteriaSupportable>(String name, F filter, Pagination pagination) {

    public boolean isListQuery() {
        return pagination.isPaged();
    }

    public Query toFilterQuery() {
        Query query = new Query();
        query.addCriteria(filter.toCriteria());

        if (isListQuery()) {
            query = query.with(pagination.getPageRequest());
        }

        log.info("Filter query: {}", query);

        return query;
    }

    public Query toCountQuery() {
        Query query = new Query();
        query.addCriteria(filter.toCriteria());

        log.info("Count query: {}", query);

        return query;
    }

    public record Pagination(int pageSize, int pageNumber, int rangeStart, int rangeEnd) {
        public PageRequest getPageRequest() {
            return PageRequest.of(pageNumber, pageSize);
        }

        public boolean isPaged() {
            return pageSize > 0;
        }
    }
}
