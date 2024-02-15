package pro.b2borganizer.services.reports.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MailMonthlyReportsFilter {

    private Integer month;

    private Integer year;

}
