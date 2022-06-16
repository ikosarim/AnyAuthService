package ru.any.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.any.auth.dto.PhoneNumberDto;
import ru.any.auth.dto.SendingResultDto;
import ru.any.auth.feign.SmsCenterFeignClient;

@Slf4j
@Service
public class SmsCenterSmsSenderService implements SmsSenderService {

    @Value("${sms.center.login}")
    private String smsCenterLogin;
    @Value("${sms.center.psw}")
    private String smsCenterPsw;

    private final SmsCenterFeignClient smsCenterFeignClient;

    public SmsCenterSmsSenderService(SmsCenterFeignClient smsCenterFeignClient) {
        this.smsCenterFeignClient = smsCenterFeignClient;
    }


    @Override
    public SendingResultDto sendCodeInSms(PhoneNumberDto phoneNumberDto, String secret) {
        if (secret != null) {
            return new SendingResultDto().result(true).message(secret);
        }
        ResponseEntity<String> responseEntity;
        try {
            // TODO: 13.06.2022 Заменить на просто отправку
            final String phone = phoneNumberDto.getPhone();
            responseEntity = smsCenterFeignClient.getSmsCost(smsCenterLogin, smsCenterPsw, phone, secret, "1");
            final HttpStatus statusCode = responseEntity.getStatusCode();
            if (statusCode.is4xxClientError() || statusCode.is5xxServerError()) {
                log.error("Error in sending sms. Status code is {}", statusCode.toString());
                final String message = responseEntity.getBody();
                log.error("Error message is {}", message);
                return new SendingResultDto().result(false).message(message);
            }
        } catch (Exception e) {
            log.error("Unknown response from service");
            return new SendingResultDto().result(false).message("Error unknown");
        }
        return new SendingResultDto().result(false).message(responseEntity.getBody());
    }
}
