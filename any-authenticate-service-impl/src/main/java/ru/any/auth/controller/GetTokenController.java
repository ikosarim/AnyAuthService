package ru.any.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.any.auth.dto.PhoneNumberDto;
import ru.any.auth.dto.SendingResultDto;
import ru.any.auth.dto.SmsCodeDto;

@RestController
public class GetTokenController implements GetTokenApi {

    @Override
    public ResponseEntity<SendingResultDto> sendCode(SmsCodeDto smsCodeDto) {
        return ResponseEntity.ok(new SendingResultDto().result(true).message(""));
    }

    @Override
    public ResponseEntity<SendingResultDto> sendPhone(PhoneNumberDto phoneNumberDto) {
        return ResponseEntity.ok(new SendingResultDto().message("success"));
    }
}
