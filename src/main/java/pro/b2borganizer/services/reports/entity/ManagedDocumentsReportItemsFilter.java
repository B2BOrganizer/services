package pro.b2borganizer.services.reports.entity;

import org.springframework.data.mongodb.core.query.Criteria;
import pro.b2borganizer.services.common.entity.CriteriaSupportable;

public record ManagedDocumentsReportItemsFilter(String managedDocumentsReportId) implements CriteriaSupportable {
    @Override
    public Criteria toCriteria() {
        Criteria criteria = new Criteria();
        if (managedDocumentsReportId != null) {
            criteria.and("managedDocumentsReportId").is(managedDocumentsReportId);
        }
        return criteria;
    }
}
