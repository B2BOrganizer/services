package pro.b2borganizer.services.reports.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NewManagedDocumentsReport {

    @NotNull
    private ManagedDocumentsReportInterval interval;

    @NotNull
    private Integer year;

    @NotNull
    private Integer month;
}
