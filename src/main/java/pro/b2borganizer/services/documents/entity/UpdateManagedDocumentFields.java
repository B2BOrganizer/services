package pro.b2borganizer.services.documents.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
@ToString
public class UpdateManagedDocumentFields {

    private JsonNullable<String> comment;
}
