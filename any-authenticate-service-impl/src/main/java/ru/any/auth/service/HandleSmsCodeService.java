package ru.any.auth.service;

import ru.any.auth.dto.PhoneNumberDto;
import ru.any.auth.dto.SendingResultDto;
import ru.any.auth.dto.SmsCodeDto;

public interface HandleSmsCodeService {
    String generateSmsCode(PhoneNumberDto phoneNumberDto);

    SendingResultDto checkSecretCode(SmsCodeDto smsCodeDto);
}
