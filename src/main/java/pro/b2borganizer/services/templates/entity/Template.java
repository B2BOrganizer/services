package pro.b2borganizer.services.templates.entity;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("templates")
@Getter
@Setter
public class Template {
    @Id
    private String id;

    @NotNull
    private String code;

    @NotNull
    private String name;

    @NotNull
    private TemplateType templateType;

    @NotNull
    private String path;

    @NotNull
    private String contentType;

    private Set<TemplateVariable> variables;
}

