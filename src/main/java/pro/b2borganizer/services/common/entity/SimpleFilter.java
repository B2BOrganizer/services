package pro.b2borganizer.services.common.entity;

import java.net.CookieManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;

public record SimpleFilter(Map<SimpleFilterKey, Object> filters) implements CriteriaSupportable {
    public enum KeyType {
        EQUALS,
        GREATER_THAN,
        LESS_THAN,
        LESS_THAN_EQUALS,
        IN,
    }

    public record SimpleFilterKey (String key, KeyType type) {

        public Criteria toCriteria(Object value) {
            return switch (type) {
                case EQUALS -> Criteria.where(key).is(value);
                case IN -> Criteria.where(key).in(value);
                case GREATER_THAN -> Criteria.where(key).gt(value);
                case LESS_THAN -> Criteria.where(key).lt(value);
                case LESS_THAN_EQUALS -> Criteria.where(key).lte(value);
            };
        }
    }

    @Override
    public Criteria toCriteria() {
        Criteria criteria = new Criteria();

        List<List<Criteria>> groupedCriteria = filters.entrySet().stream()
                .collect(Collectors.groupingBy(e -> e.getKey().key))
                .values()
                .stream()
                .map(v -> v.stream().map(e -> e.getKey().toCriteria(e.getValue())).toList())
                .toList();

        List<Criteria> allCriteria = groupedCriteria.stream()
                .flatMap(List::stream)
                .toList();

        criteria.andOperator(allCriteria.toArray(new Criteria[0]));

        return criteria;
    }
}
