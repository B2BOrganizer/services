package pro.b2borganizer.services.reports.entity;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
public class NewPredefinedMailMonthlyReport {

    @NotNull
    private String sendTo;

    private String copyTo;

    @NotNull
    private String subject;

    @NotNull
    private String templateCode;
}
