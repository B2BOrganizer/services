package pro.b2borganizer.services.reports.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UpdatedPredefinedMailMonthlyReport {

    @NotNull
    private String sendTo;

    private String copyTo;

    @NotNull
    private String subject;

    @NotNull
    private String templateCode;
}
