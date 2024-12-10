package pro.b2borganizer.services.documents.entity;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManagedDocumentPreviews {

    private String id;

    private List<String> previews;
}
