package pro.b2borganizer.services.tokens.boundary;

import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.b2borganizer.services.tokens.control.APITokensRepository;
import pro.b2borganizer.services.tokens.entity.APIToken;
import pro.b2borganizer.services.tokens.entity.GenerateTokenRequest;
import pro.b2borganizer.services.users.control.UsersRepository;
import pro.b2borganizer.services.users.entity.AuthenticationRequest;

@RestController
@RequestMapping("/api-tokens")
@RequiredArgsConstructor
@Slf4j
public class APITokensResource {

    private final UsersRepository usersRepository;

    private final APITokensRepository apiTokensRepository;

    @PostMapping
    public ResponseEntity<String> create(@RequestBody GenerateTokenRequest generateTokenRequest) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        String username = authentication.getName();

        if (!generateTokenRequest.getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Optional<APIToken> foundAPIToken = apiTokensRepository.findByUsernameAndActiveTrue(username);

        if (foundAPIToken.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            APIToken apiToken = new APIToken();
            apiToken.setUsername(username);
            apiToken.setToken(UUID.randomUUID().toString());
            apiToken.setActive(true);
            apiTokensRepository.save(apiToken);
            return ResponseEntity.status(HttpStatus.CREATED).body(apiToken.getToken());
        }
    }
}
