package ru.any.auth.config;

import lombok.Builder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class JWTPreAuthenticationToken extends PreAuthenticatedAuthenticationToken {

    @Builder
    public JWTPreAuthenticationToken(String principal, WebAuthenticationDetails details) {
        super(principal, null);
        super.setDetails(details);
    }

    @Override
    public Object getCredentials() {
        return null;
    }
}
