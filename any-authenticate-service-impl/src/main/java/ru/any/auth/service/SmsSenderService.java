package ru.any.auth.service;

import ru.any.auth.dto.PhoneNumberDto;
import ru.any.auth.dto.SendingResultDto;

public interface SmsSenderService {

    SendingResultDto sendCodeInSms(PhoneNumberDto phoneNumberDto, String secret);
}
