package pro.b2borganizer.services.users.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AuthenticationRequest {
    private String username;

    @ToString.Exclude
    private String password;
}
