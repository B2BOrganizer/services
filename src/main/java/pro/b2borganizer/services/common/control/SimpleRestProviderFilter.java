package pro.b2borganizer.services.common.control;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Query;
import pro.b2borganizer.services.common.entity.CriteriaSupportable;
import pro.b2borganizer.services.common.entity.SimpleFilter;
import pro.b2borganizer.services.common.entity.SimpleSort;

@Slf4j
public record SimpleRestProviderFilter(String name, SimpleFilter simpleFilter, Pagination pagination, SimpleSort simpleSort) {

    public boolean isListQuery() {
        return pagination.isPaged();
    }

    public Query toFilterQuery() {
        Query query = new Query();
        query.addCriteria(simpleFilter.toCriteria());
        query.with(simpleSort.toSort());

        if (isListQuery()) {
            query = query.with(pagination.getPageRequest());
        }

        log.info("Filter query {}: {}", name, query);

        return query;
    }

    public Query toCountQuery() {
        Query query = new Query();
        query.addCriteria(simpleFilter.toCriteria());

        log.info("Count query {}: {}", name, query);

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
