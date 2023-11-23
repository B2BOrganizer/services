package pro.b2borganizer.services.users.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class User {
    @Id
    private String id;

    @NotNull
    private String username;

    @ToString.Exclude
    @JsonIgnore
    @NotNull
    private String password;
}
