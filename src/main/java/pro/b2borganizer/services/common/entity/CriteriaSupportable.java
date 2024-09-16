package pro.b2borganizer.services.common.entity;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;

public interface CriteriaSupportable {

    Criteria toCriteria();

    Sort toSort();
}
