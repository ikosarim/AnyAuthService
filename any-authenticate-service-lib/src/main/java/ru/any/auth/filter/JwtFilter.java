package ru.any.auth.filter;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.any.auth.config.JWTPreAuthenticationToken;
import ru.any.auth.service.TokenGenerator;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final Pattern BEARER_PATTERN = Pattern.compile("^Bearer (.+?)$");

    private final TokenGenerator tokenGenerator;

    public JwtFilter(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        getToken(request).map(
                token -> {
                    if (tokenGenerator.validateAccessToken(token)) {
                        throw new IllegalArgumentException("Invalid token");
                    }
                    return tokenGenerator.getAccessClaims(token).getSubject();
                }
        ).map(phone -> buildPreAuthenticationToken(request, phone))
                .ifPresent(authentication -> SecurityContextHolder.getContext().setAuthentication(authentication));
        try {
            filterChain.doFilter(request, response);
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }
    }

    private JWTPreAuthenticationToken buildPreAuthenticationToken(HttpServletRequest request, String phone) {
        return JWTPreAuthenticationToken.builder()
                .principal(phone)
                .details(new WebAuthenticationDetailsSource().buildDetails(request))
                .build();
    }

    private Optional<String> getToken(HttpServletRequest request) {
        return ofNullable(request.getHeader(AUTHORIZATION_HEADER))
                .filter(not(s -> s != null && s.isEmpty()))
                .map(BEARER_PATTERN::matcher)
                .filter(Matcher::find)
                .map(matcher -> matcher.group(1));
    }
}