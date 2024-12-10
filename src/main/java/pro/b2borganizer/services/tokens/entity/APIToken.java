package pro.b2borganizer.services.tokens.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
@ToString
public class APIToken {
    @Id
    private String id;

    @NotNull
    private String username;

    @NotNull
    private String token;

    private boolean active;
}
