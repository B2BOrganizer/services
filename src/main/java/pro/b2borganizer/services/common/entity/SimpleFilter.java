package pro.b2borganizer.services.common.entity;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;

public record SimpleFilter(Map<String, Object> filters) implements CriteriaSupportable {
    @Override
    public Criteria toCriteria() {
        Criteria criteria = new Criteria();
        filters.forEach((key, value) -> {
            if (value instanceof List) {
                criteria.and(key).in(value);
            } else {
                criteria.and(key).is(value);
            }
        });
        return criteria;
    }
}
