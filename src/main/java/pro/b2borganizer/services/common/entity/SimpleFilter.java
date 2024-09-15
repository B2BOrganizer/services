package pro.b2borganizer.services.common.entity;

import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;

public record SimpleFilter(List<String> id) implements CriteriaSupportable {
    @Override
    public Criteria toCriteria() {
        Criteria criteria = new Criteria();
        if (id != null) {
            criteria.and("id").in(id);
        }
        return criteria;
    }
}
