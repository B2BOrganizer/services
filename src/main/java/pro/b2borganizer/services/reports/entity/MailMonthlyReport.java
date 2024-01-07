package pro.b2borganizer.services.reports.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pro.b2borganizer.services.documents.entity.ManagedDocument;

@Document("mailMonthlyReports")
@Getter
@Setter
@ToString
public class MailMonthlyReport {

    @Id
    private String id;

    @NotNull
    private MailMonthlyReportStatus status;

    @NotNull
    private LocalDateTime created;

    private LocalDateTime sent;

    @NotNull
    private String sendTo;

    private String copyTo;

    @NotNull
    private String templateId;

    @NotNull
    private String contentType;

    private Map<String, Object> templateVariables;

    @NotNull
    private String subject;

    @NotNull
    private int month;

    @NotNull
    private int year;

    private List<ManagedDocument> managedDocuments;
}
