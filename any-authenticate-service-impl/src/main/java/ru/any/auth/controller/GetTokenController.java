package ru.any.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.any.auth.dto.JwtResponseDto;
import ru.any.auth.dto.PhoneNumberDto;
import ru.any.auth.dto.SendingResultDto;
import ru.any.auth.dto.SmsCodeDto;
import ru.any.auth.service.GetTokenService;

@RestController
public class GetTokenController implements GetTokenApi {

    private final GetTokenService getTokenService;

    public GetTokenController(GetTokenService getTokenService) {
        this.getTokenService = getTokenService;
    }

    @Override
    public ResponseEntity<JwtResponseDto> sendCode(SmsCodeDto smsCodeDto) {
        final JwtResponseDto jwtResponseDto = getTokenService.checkSecretCode(smsCodeDto);
        return ResponseEntity.ok(jwtResponseDto);
    }

    @Override
    public ResponseEntity<SendingResultDto> sendPhone(PhoneNumberDto phoneNumberDto) {
        if (!getTokenService.checkIsNumberValidAndIsRussian(phoneNumberDto)) {
            final SendingResultDto sendingResultDto = new SendingResultDto().isSuccessful(false)
                    .errorMessage("Неправильный формат номера телефона");
            return ResponseEntity.ok(sendingResultDto);
        }
        final SendingResultDto sendingResultDto = getTokenService.sendSmsWithCode(phoneNumberDto);
        return ResponseEntity.ok(sendingResultDto);
    }
}
