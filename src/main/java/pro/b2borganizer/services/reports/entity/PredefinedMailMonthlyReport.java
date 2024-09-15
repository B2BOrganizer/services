package pro.b2borganizer.services.reports.entity;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("predefinedMailMonthlyReports")
@Getter
@Setter
@ToString
public class PredefinedMailMonthlyReport {

    @Id
    private String id;

    @NotNull
    private String sendTo;

    private String copyTo;

    @NotNull
    private String subject;

    @NotNull
    private String templateCode;

    public String getSubjectParsed() {
        LocalDate now = LocalDate.now();

        return subject
                .replace("${currentMonth}", "" + now.getMonth().getValue())
                .replace("${currentYear}", "" + now.getYear())
                .replace("${previousMonth}", "" + now.minusMonths(1).getMonth().getValue())
                .replace("${previousYear}", "" + now.minusYears(1).getYear())
                ;
    }

}
