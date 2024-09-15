package pro.b2borganizer.services.reports.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pro.b2borganizer.services.documents.entity.RequiredDocumentInterval;

@Document("managedDocumentsReports")
@Getter
@Setter
@ToString
public class ManagedDocumentsReport {

    @Id
    private String id;

    @NotNull
    private ManagedDocumentsReportInterval interval;

    @NotNull
    private Integer year;

    @NotNull
    private Integer month;
}
