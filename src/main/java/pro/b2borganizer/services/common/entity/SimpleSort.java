package pro.b2borganizer.services.common.entity;

import org.springframework.data.domain.Sort;

public record SimpleSort(String field, SortDirection direction) {

    public enum SortDirection {
        ASC,
        DESC
    }

    public Sort toSort() {
        return Sort.by(Sort.Direction.valueOf(direction.name()), field);
    }
}
