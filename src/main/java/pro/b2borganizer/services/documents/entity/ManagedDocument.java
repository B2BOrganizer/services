package pro.b2borganizer.services.documents.entity;

import java.time.LocalDateTime;
import java.time.YearMonth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pro.b2borganizer.services.files.entity.ManagedFile;

@Document("managedDocuments")
@Getter
@Setter
@ToString
public class ManagedDocument {
    @Id
    private String id;

    private String mailMessageId;

    private ManagedFile managedFile;

    private LocalDateTime sent;

    private LocalDateTime received;

    private Integer assignedToYear;

    private Integer assignedToMonth;

    private String comment;

    public boolean isCommented() {
        return StringUtils.isNotEmpty(comment);
    }
}