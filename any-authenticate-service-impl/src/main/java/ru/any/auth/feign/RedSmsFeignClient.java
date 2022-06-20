package ru.any.auth.feign;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import ru.any.auth.dto.RedSmsRequest;
import ru.any.auth.dto.RedSmsResponse;

@FeignClient(name = "red-sms-feign", url = "${red.sms.url}")
public interface RedSmsFeignClient {

    @Headers({
            "Content-type: application/json",
            "login: {login}",
            "ts: {ts}",
            "secret: {secret}"
    })
    @RequestLine("POST ")
    ResponseEntity<RedSmsResponse> sendCodeSms(
            @Param("login") String login,
            @Param("ts") String ts,
            @Param("secret") String secret,
            @RequestBody RedSmsRequest redSmsRequest
    );
}
