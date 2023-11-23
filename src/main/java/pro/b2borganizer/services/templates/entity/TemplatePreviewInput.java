package pro.b2borganizer.services.templates.entity;

import java.util.Map;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TemplatePreviewInput {

    @NotNull
    private String templateCode;

    private Map<String, Object> variables;
}
