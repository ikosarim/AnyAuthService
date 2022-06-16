package ru.any.auth.service;

import ru.any.auth.dto.JwtResponseDto;
import ru.any.auth.dto.PhoneNumberDto;
import ru.any.auth.dto.SendingResultDto;
import ru.any.auth.dto.SmsCodeDto;

public interface GetTokenService {

    Boolean checkIsNumberValidAndIsRussian(PhoneNumberDto phoneNumberDto);

    SendingResultDto sendSmsWithCode(PhoneNumberDto phoneNumberDto);

    JwtResponseDto checkSecretCode(SmsCodeDto smsCodeDto);
}
