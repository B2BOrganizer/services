package pro.b2borganizer.services.documents.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ManagedDocumentPreviewsGenerationEvent {

    private final Type type;

    public static enum Type {
        ALL
    }
}
