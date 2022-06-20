package ru.any.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Contract;
import feign.Logger;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.any.auth.dto.RedSmsException;

import java.io.IOException;

@Configuration
public class FeignConfig {

    @Bean
    Logger.Level feignLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Contract feignContract() {
        return new feign.Contract.Default();
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return ((methodKey, response) -> {
            final ObjectMapper objectMapper = new ObjectMapper();
            RedSmsException redSmsException = new RedSmsException();
            try {
                redSmsException = objectMapper.readValue(response.body().asInputStream(), RedSmsException.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return redSmsException;
        });
    }
}
