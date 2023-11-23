package pro.b2borganizer.services.users.boundary;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.b2borganizer.services.users.control.JwtTokenProvider;
import pro.b2borganizer.services.users.entity.AuthenticationRequest;

@RestController
@RequestMapping("/authentications")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationResource {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public ResponseEntity<String> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));

        String token = jwtTokenProvider.createToken(authenticate.getName());

        return ResponseEntity.ok(token);
    }
}
