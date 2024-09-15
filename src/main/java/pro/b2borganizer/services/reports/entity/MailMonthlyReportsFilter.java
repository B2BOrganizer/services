package pro.b2borganizer.services.reports.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.query.Criteria;
import pro.b2borganizer.services.common.entity.CriteriaSupportable;

@Getter
@Setter
@ToString
public class MailMonthlyReportsFilter implements CriteriaSupportable {

    private Integer month;

    private Integer year;

    @Override
    public Criteria toCriteria() {
        Criteria criteria = new Criteria();
        if (year != null) {
            criteria.and("year").is(year);
        }
        if (month != null) {
            criteria.and("month").is(month);
        }

        return criteria;
    }
}
