package pro.b2borganizer.services.documents.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NewRequiredDocument {

    @NotNull
    private String name;

    @NotNull
    private RequiredDocumentInterval interval;
}
