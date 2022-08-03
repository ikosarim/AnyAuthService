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
import ru.any.auth.dto.PhoneNumberDto
import ru.any.auth.dto.SendingResultDto
import ru.any.auth.model.SecretCode
import ru.any.auth.repository.SecretCodeRepository
import ru.any.auth.service.SmsSenderService
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GenerateAndSaveSecretCodeTest : AbstractIntegrationTest() {

    companion object {
        const val BASE_URL = "/api/v1/auth/send_phone"
    }

    @MockkBean
    private lateinit var smsSenderService: SmsSenderService;

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var secretCodeRepository: SecretCodeRepository

    @BeforeEach
    fun beforeEachTest() {
        secretCodeRepository.deleteAll()
        every { smsSenderService.sendCodeInSms(allAny(), allAny()) } returns SendingResultDto().result(true)
    }

    @Test
    fun notCorrectPhoneNotBeInDB() {
        val phoneNumber = "798ert"
        sendPhoneToService(phoneNumber)
        val secretCodeList = secretCodeRepository.findAll();
        assertNotNull(secretCodeList)
        assertEquals(0, secretCodeList.size)
    }

    @Test
    fun newPhoneWillBeInDB() {
        val phoneNumber = "79116547813"
        sendPhoneToService(phoneNumber)
        val secretCodeList = secretCodeRepository.findAll()
        assertNotNull(secretCodeList)
        assertEquals(1, secretCodeList.size)
        val secretCode = secretCodeList[0];
        assertNotNull(secretCode)
        assertEquals(phoneNumber, secretCode.phone)
        assertNotNull(secretCode.secret)
        assertEquals(0, secretCode.attemptsNumber)
        assertNull(secretCode.delayUntil)
        assertNotNull(secretCode.expireDateTime)
    }

    @Test
    fun newPhoneWillBeInDBWithOlds() {
        val oldSecretCode = SecretCode.builder()
            .phone("79456127799")
            .secret("789213")
            .attemptsNumber(0)
            .delayUntil(null)
            .expireDateTime(LocalDateTime.now().plusMinutes(5))
            .build()
        secretCodeRepository.save(oldSecretCode)
        val phoneNumber = "79116547813"
        sendPhoneToService(phoneNumber)
        val secretCodeList = secretCodeRepository.findAll()
        assertNotNull(secretCodeList)
        assertEquals(2, secretCodeList.size)
    }

    @Test
    fun newPhoneSecretUpdateExisting() {
        val oldSecret = "789213"
        val oldExpireDateTime = LocalDateTime.now().plusMinutes(5)
        val oldSecretCode = SecretCode.builder()
            .phone("79116547813")
            .secret(oldSecret)
            .attemptsNumber(2)
            .delayUntil(LocalDateTime.now().plusSeconds(40))
            .expireDateTime(oldExpireDateTime)
            .build()
        secretCodeRepository.save(oldSecretCode)
        val phoneNumber = "79116547813"
        sendPhoneToService(phoneNumber)
        val secretCodeList = secretCodeRepository.findAll()
        assertNotNull(secretCodeList)
        assertEquals(1, secretCodeList.size)
        val secretCode = secretCodeList[0]
        assertNotNull(secretCode)
        assertEquals(phoneNumber, secretCode.phone)
        assertNotEquals(oldSecret, secretCode.secret)
        assertEquals(0, secretCode.attemptsNumber)
        assertNull(secretCode.delayUntil)
        assertNotEquals(oldExpireDateTime, secretCode.expireDateTime)
    }

    private fun sendPhoneToService(phoneNumber: String) {
        val phone = PhoneNumberDto().phone(phoneNumber);
        val request = HttpEntity(phone);
        restTemplate.exchange<SendingResultDto>(
            CheckValidNumberTest.BASE_URL,
            HttpMethod.POST, request, SendingResultDto::class
        )
    }
}