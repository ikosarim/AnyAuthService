package ru.any.auth.service;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.any.auth.SecretCode;
import ru.any.auth.dto.PhoneNumberDto;
import ru.any.auth.dto.SendingResultDto;
import ru.any.auth.dto.SmsCodeDto;
import ru.any.auth.repository.SecretCodeRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class HandleSmsCodeServiceImpl implements HandleSmsCodeService {

    private final SecretCodeRepository secretCodeRepository;

    public HandleSmsCodeServiceImpl(SecretCodeRepository secretCodeRepository) {
        this.secretCodeRepository = secretCodeRepository;
    }

    @Override
    public String generateSmsCode(PhoneNumberDto phoneNumberDto) {
        final SecretCode secretCode = secretCodeRepository.findByPhone(phoneNumberDto.getPhone())
                .map(secret -> secret.setExpireDateTime(LocalDateTime.now().plusMinutes(1L))
                        .setAttemptsNumber(0L)
                        .setDelayUntil(null)
                        .setSecret(String.valueOf(RandomUtils.nextLong(100000, 999999)))
                ).orElseGet(() -> secretCodeRepository.save(createNewSecretCode(phoneNumberDto)));
        return secretCode.getSecret();
    }

    private SecretCode createNewSecretCode(PhoneNumberDto phoneNumberDto) {
        return SecretCode.builder()
                .phone(phoneNumberDto.getPhone())
                .secret(String.valueOf(RandomUtils.nextLong(100000, 999999)))
                .expireDateTime(LocalDateTime.now().plusMinutes(1L))
                .attemptsNumber(0L)
                .delayUntil(null)
                .build();
    }

    @Override
    public SendingResultDto checkSecretCode(SmsCodeDto smsCodeDto) {
        final String phone = smsCodeDto.getPhone();
        final Optional<SecretCode> secretCodeOptional = secretCodeRepository.findByPhone(phone);
        if (secretCodeOptional.isEmpty()) {
            return new SendingResultDto().result(false).message("Нет кода для этого телефона. Запросите новый.");
        }
        final SecretCode secretCode = secretCodeOptional.get();
        if (LocalDateTime.now().isAfter(secretCode.getExpireDateTime())) {
            return new SendingResultDto().result(false).message("Время действия кода истекло. Запросите новый.");
        }
        final Long attemptsNumber = secretCode.getAttemptsNumber();
        if (attemptsNumber > 3) {
            return new SendingResultDto().result(false).message("Было сделано максимальное количество попыток ввода кода. Запросите новый.");
        }
        if (attemptsNumber != 0 && LocalDateTime.now().isBefore(secretCode.getDelayUntil())) {
            return new SendingResultDto().result(false).message("5 секунд после нудачной попытки ввода еще не прошли");
        }
        if (!secretCode.getSecret().equals(smsCodeDto.getCode())) {
            secretCode.setAttemptsNumber(attemptsNumber + 1);
            secretCode.setDelayUntil(LocalDateTime.now().plusSeconds(5L));
            return new SendingResultDto().result(false).message("Введен неправильный код");
        }
        return new SendingResultDto().result(true).message("Аутентификация прошла успешно");
    }
}
