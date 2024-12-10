package pro.b2borganizer.services.tokens.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateTokenRequest {
    @NotNull
    private String username;
}
