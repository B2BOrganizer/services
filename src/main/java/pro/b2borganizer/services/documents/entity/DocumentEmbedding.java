package pro.b2borganizer.services.documents.entity;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("documentEmbeddings")
@Getter
@Setter
@ToString
public class DocumentEmbedding {
    @Id
    private String id;

    @Indexed
    private String managedDocumentId;

    private List<Double> embedding;

    @ToString.Exclude
    private String textContent;

    private LocalDateTime createdAt;

    private String requiredDocumentId;
}
