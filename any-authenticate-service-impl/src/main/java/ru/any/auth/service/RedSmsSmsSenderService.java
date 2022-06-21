package ru.any.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import ru.any.auth.dto.PhoneNumberDto;
import ru.any.auth.dto.RedSmsException;
import ru.any.auth.dto.RedSmsRequest;
import ru.any.auth.dto.SendingResultDto;
import ru.any.auth.feign.RedSmsFeignClient;

@Slf4j
@Service
public class RedSmsSmsSenderService implements SmsSenderService {

    @Value("${red.sms.login}")
    private String redSmsLogin;
    @Value("${red.sms.api_key}")
    private String redSmsApiKey;
    @Value("${red.sms.route}")
    private String redSmsRoute;
    @Value("${red.sms.from}")
    private String redSmsFrom;
    @Value("${red.sms.message_template}")
    private String redSmsMessageTemplate;

    private final RedSmsFeignClient redSmsFeignClient;

    public RedSmsSmsSenderService(RedSmsFeignClient redSmsFeignClient) {
        this.redSmsFeignClient = redSmsFeignClient;
    }

    @Override
    public SendingResultDto sendCodeInSms(PhoneNumberDto phoneNumberDto, String code) {
        if (code == null) {
            return new SendingResultDto().result(false).message("Ошибка при генерации кода");
        }
        final String phone = phoneNumberDto.getPhone();
        final RedSmsRequest redSmsRequest = RedSmsRequest.builder()
                .route(redSmsRoute)
                .from(redSmsFrom)
                .text(redSmsMessageTemplate + ": " + code)
                .to(phone)
                .build();
        final Long ts = RandomUtils.nextLong(10000, 999999999);
        final String secret = DigestUtils.md5DigestAsHex((ts + redSmsApiKey).getBytes());
        try {
            redSmsFeignClient.sendCodeSms(redSmsLogin, ts.toString(), secret, redSmsRequest);
        } catch (Exception e) {
            log.error("Unknown response from service");
            final RedSmsException redSmsException = (RedSmsException) e.getCause();
            return new SendingResultDto().result(false).message(redSmsException.getErrorMessage());
        }
        return new SendingResultDto().result(true);
    }
}
