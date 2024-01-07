package pro.b2borganizer.services.reports.entity;

import java.time.Month;
import java.time.Year;
import java.util.Map;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NewMailMonthlyReport {
    @NotNull
    private String sendTo;

    private String copyTo;

    @NotNull
    private String templateCode;

    @NotNull
    private String subject;

    @NotNull
    private Integer month;

    @NotNull
    private Integer year;

    private Map<String, Object> templateVariables;
}
