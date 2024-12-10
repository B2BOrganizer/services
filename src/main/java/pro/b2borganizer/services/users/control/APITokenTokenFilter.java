package pro.b2borganizer.services.users.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pro.b2borganizer.services.tokens.control.APITokensRepository;
import pro.b2borganizer.services.tokens.entity.APIToken;

@Component
@Slf4j
@RequiredArgsConstructor
public class APITokenTokenFilter extends OncePerRequestFilter {

    private final APITokensRepository apiTokensRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);

        if (token != null) {
            Optional<APIToken> foundAPIToken = apiTokensRepository.findByTokenAndActiveTrue(token);

            foundAPIToken.ifPresent(apiToken -> {
                Authentication authentication = new UsernamePasswordAuthenticationToken(apiToken.getUsername(),"",new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            });
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest req) {
        String apiToken = req.getHeader("API-Token");
        if (apiToken != null && apiToken.startsWith("Bearer ")) {
            return apiToken.substring(7);
        }
        return null;
    }
}
