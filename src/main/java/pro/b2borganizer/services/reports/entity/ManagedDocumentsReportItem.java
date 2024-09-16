package pro.b2borganizer.services.reports.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pro.b2borganizer.services.files.entity.ManagedFile;

@Getter
@Setter
@Document("managedDocumentsReportItem")
public class ManagedDocumentsReportItem {

    @Id
    private String id;

    @NotNull
    private String managedDocumentsReportId;

    private String requiredDocumentId;

    private String managedDocumentId;

    private String requiredDocumentName;

    private LocalDateTime managedDocumentReceived;

    private String managedDocumentFileName;

    private List<ManagedFile> managedDocumentPreviews;

    public boolean hasRequiredDocument() {
        return requiredDocumentId != null;
    }

    public boolean hasManagedDocument() {
        return managedDocumentId != null;
    }

    public boolean isRequiredDocumentFound() {
        return hasRequiredDocument() && hasManagedDocument();
    }
}
