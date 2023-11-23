package pro.b2borganizer.services.reports.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class MailMonthlyReportCreatedEvent {

    private String mailMonthlyReportId;
}
