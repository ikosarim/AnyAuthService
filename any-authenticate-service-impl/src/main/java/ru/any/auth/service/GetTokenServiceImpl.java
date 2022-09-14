package ru.any.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.any.auth.dto.JwtResponseDto;
import ru.any.auth.dto.PhoneNumberDto;
import ru.any.auth.dto.SendingResultDto;
import ru.any.auth.dto.SmsCodeDto;

import java.util.Objects;
import java.util.regex.Pattern;

@Slf4j
@Service
public class GetTokenServiceImpl implements GetTokenService {

    private final SmsSenderService smsSenderService;
    private final HandleSmsCodeService handleSmsCodeService;
    private final TokenGenerator tokenGenerator;

    public GetTokenServiceImpl(
            SmsSenderService smsSenderService,
            HandleSmsCodeService handleSmsCodeService,
            TokenGenerator tokenGenerator
    ) {
        this.smsSenderService = smsSenderService;
        this.handleSmsCodeService = handleSmsCodeService;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public Boolean checkIsNumberValidAndIsRussian(PhoneNumberDto phoneNumberDto) {
        final String phone = phoneNumberDto.getPhone();
        if (Objects.isNull(phone)) {
            return false;
        }
        String clearPhone = phone.replaceAll("\\+", "")
                .replaceAll("\\(", "")
                .replaceAll("\\)", "")
                .replaceAll("-", "");
        if (!Pattern.compile("\\d+").matcher(clearPhone).matches()) {
            return false;
        }
        if (clearPhone.startsWith("8")) {
            clearPhone = clearPhone.replaceFirst("8", "7");
        }
        phoneNumberDto.setPhone(clearPhone);
        return clearPhone.startsWith("7") && clearPhone.length() == 11;
    }

    @Override
    public SendingResultDto sendSmsWithCode(PhoneNumberDto phoneNumberDto) {
        String secret = handleSmsCodeService.generateSmsCode(phoneNumberDto);
        return smsSenderService.sendCodeInSms(phoneNumberDto, secret);
    }

    @Override
    public JwtResponseDto checkSecretCode(SmsCodeDto smsCodeDto) {
        final SendingResultDto sendingResultDto = handleSmsCodeService.checkSecretCode(smsCodeDto);
        if (!sendingResultDto.getIsSuccessful()) {
            return new JwtResponseDto().isSuccessful(false).errorMessage(sendingResultDto.getErrorMessage());
        }
        final String phone = smsCodeDto.getPhone();
        final String accessToken = tokenGenerator.generateAccessToken(phone);
        final String refreshToken = tokenGenerator.generateRefreshToken(phone);
        return new JwtResponseDto().isSuccessful(true).accessToken(accessToken).refreshToken(refreshToken);
    }
}