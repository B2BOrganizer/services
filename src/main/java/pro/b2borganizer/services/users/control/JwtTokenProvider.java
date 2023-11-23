package pro.b2borganizer.services.users.control;

import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expirationInMillis}")
    private long validityInMilliseconds;

    public String createToken(String username) {
        Date tokenCreated = new Date();
        Date tokenValidity = new Date(tokenCreated.getTime() + validityInMilliseconds);

        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

        return Jwts.builder()
                .claims().subject(username).issuedAt(tokenCreated).expiration(tokenValidity)
                .and()
                .signWith(key)
                .compact();
    }

    public JwtTokenValidator getValidator(String token) {
        return new JwtTokenValidator(token);
    }

    public class JwtTokenValidator {
        private final Jws<Claims> claims;

        public JwtTokenValidator(String token) {
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
            this.claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
        }

        public boolean isValid() {
            return claims.getPayload().getExpiration().after(new Date());
        }

        public String getUsername() {
            return claims.getPayload().getSubject();
        }
    }

}
