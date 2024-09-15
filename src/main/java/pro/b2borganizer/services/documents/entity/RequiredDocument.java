package pro.b2borganizer.services.documents.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("requiredMonthlyDocuments")
@Getter
@Setter
@ToString
public class RequiredDocument {
    @Id
    private String id;

    @NotNull
    private String name;

    @NotNull
    private RequiredDocumentInterval interval = RequiredDocumentInterval.MONTHLY;

    @NotNull
    private RequiredDocumentType type = RequiredDocumentType.SINGLE;
}
