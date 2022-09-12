package ru.any.auth

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import ru.any.auth.dto.JwtResponseDto
import ru.any.auth.dto.SmsCodeDto
import ru.any.auth.model.SecretCode
import ru.any.auth.repository.SecretCodeRepository
import ru.any.auth.service.TokenGenerator
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CheckSecretCodeTest : AbstractIntegrationTest() {

    companion object {
        const val BASE_URL = "/api/v1/auth/send_code"
    }

    @MockkBean
    private lateinit var tokenGenerator: TokenGenerator

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var secretCodeRepository: SecretCodeRepository

    @BeforeEach
    fun beforeEachTest() {
        secretCodeRepository.deleteAll()
        every { tokenGenerator.generateAccessToken(any()) } returns "access"
        every { tokenGenerator.generateRefreshToken(any()) } returns "refresh"
    }

    @Test
    fun returnErrorIfSendEmptyCode() {
        val phone = "79456127799"
        val secret = "789213"
        val jwtResponseDto = buildSecretCodeAndSendRequest(
            phone,
            secret,
            secret,
            LocalDateTime.now().plusMinutes(5),
            0,
            false
        )
        assertNull(jwtResponseDto.accessToken)
        assertNull(jwtResponseDto.refreshToken)
        assertNotNull(jwtResponseDto.errorMessage)
        assertEquals("Нет кода для этого телефона. Запросите новый.", jwtResponseDto.errorMessage)
    }

    @Test
    fun returnErrorIfExpireDateOfSecretIsOverdue() {
        val phone = "79456127799"
        val secret = "789213"
        val jwtResponseDto = buildSecretCodeAndSendRequest(
            phone,
            secret,
            secret,
            LocalDateTime.now().minusMinutes(1),
            0,
            true
        )
        assertNull(jwtResponseDto.accessToken)
        assertNull(jwtResponseDto.refreshToken)
        assertNotNull(jwtResponseDto.errorMessage)
        assertEquals("Время действия кода истекло. Запросите новый.", jwtResponseDto.errorMessage)
    }

    @Test
    fun returnErrorIfAttemptsNumberIsGreaterMax() {
        val phone = "79456127799"
        val secret = "789213"
        val jwtResponseDto = buildSecretCodeAndSendRequest(
            phone,
            secret,
            secret,
            LocalDateTime.now().plusHours(1),
            16,
            true
        )
        assertNull(jwtResponseDto.accessToken)
        assertNull(jwtResponseDto.refreshToken)
        assertNotNull(jwtResponseDto.errorMessage)
        assertEquals(
            "Было сделано максимальное количество попыток ввода кода. Запросите новый.",
            jwtResponseDto.errorMessage
        )
    }

    @Test
    fun returnErrorIfCodeIsWrong() {
        val phone = "79456127799"
        val secret = "789213"
        val jwtResponseDto = buildSecretCodeAndSendRequest(
            phone,
            secret,
            "123456",
            LocalDateTime.now().plusHours(1),
            0,
            true
        )
        assertNull(jwtResponseDto.accessToken)
        assertNull(jwtResponseDto.refreshToken)
        assertNotNull(jwtResponseDto.errorMessage)
        assertEquals(
            "Введен неправильный код",
            jwtResponseDto.errorMessage
        )
        val secretCode = secretCodeRepository.findByPhone(phone).orElseThrow()
        assertEquals(1, secretCode.attemptsNumber)
    }

    @Test
    fun returnAccessAndRefreshCodesIfCodeIsRight() {
        val phone = "79456127799"
        val secret = "789213"
        val jwtResponseDto = buildSecretCodeAndSendRequest(
            phone,
            secret,
            secret,
            LocalDateTime.now().plusHours(1),
            0,
            true
        )
        assertNotNull(jwtResponseDto.accessToken)
        assertNotNull(jwtResponseDto.refreshToken)
        assertNull(jwtResponseDto.errorMessage)
    }

    private fun buildSecretCodeAndSendRequest(
        phone: String,
        secret: String,
        code: String?,
        expireDateTime: LocalDateTime,
        attemptsNumber: Long,
        isPresent: Boolean
    ): JwtResponseDto {
        val secretCode = SecretCode.builder()
            .phone(phone)
            .secret(secret)
            .attemptsNumber(attemptsNumber)
            .expireDateTime(expireDateTime)
            .build()
        if (isPresent) {
            secretCodeRepository.save(secretCode)
        }
        val smsCode = SmsCodeDto().phone(phone).code(code)
        val request = HttpEntity(smsCode)
        val responseDto =
            restTemplate.exchange<JwtResponseDto>(BASE_URL, HttpMethod.POST, request, JwtResponseDto::class)
        assertEquals(HttpStatus.OK, responseDto.statusCode)
        val jwtResponseDto = responseDto.body
        assertNotNull(jwtResponseDto)
        return jwtResponseDto
    }
}