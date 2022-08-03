package ru.any.auth;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@ConditionalOnProperty(name = "anyAuthSecurityAutoConfiguration", prefix = "any.auth.security.enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class AnyAuthSecurityAutoConfiguration {
}
