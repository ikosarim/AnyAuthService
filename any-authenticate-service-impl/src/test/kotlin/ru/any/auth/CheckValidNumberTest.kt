package ru.any.auth

import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import io.mockk.every
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpStatus.OK
import ru.any.auth.dto.PhoneNumberDto
import ru.any.auth.dto.SendingResultDto
import ru.any.auth.service.HandleSmsCodeService
import ru.any.auth.service.SmsSenderService
import javax.validation.constraints.NotNull

class CheckValidNumberTest : AbstractIntegrationTest() {

    companion object {
        const val BASE_URL = "/api/v1/auth/send_phone"
    }

    @MockkBean
    private lateinit var handleSmsCodeService: HandleSmsCodeService
    @MockkBean
    private lateinit var smsSenderService: SmsSenderService

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @BeforeEach
    fun setMockBehavior() {
        every { handleSmsCodeService.generateSmsCode(allAny()) } returns ""
        every { smsSenderService.sendCodeInSms(allAny(), allAny()) } returns SendingResultDto().isSuccessful(true)
    }

    @Test
    fun whenSendNullReturnFalse() {
        val result = testBody(null)
        assertFalse(result!!)
    }

    @Test
    fun whenSendLettersReturnFalse() {
        val result = testBody("123asd12345")
        assertFalse(result!!)
    }

    @Test
    fun whenSendNumberStartedNotFromEightOrSevenReturnFalse() {
        val result = testBody("12345678987")
        assertFalse(result!!)
    }

    @Test
    fun whenSendNumberLengthLessElevenReturnFalse() {
        val result = testBody("7984521564")
        assertFalse(result!!)
    }

    @Test
    fun whenSendNumberLengthGreaterElevenReturnFalse() {
        val result = testBody("798452156485")
        assertFalse(result!!)
    }

    @Test
    fun whenSendNumberLengthIsElevenAndStartedWithEightReturnTrue() {
        val result = testBody("89084562312")
        assertTrue(result!!)
    }

    @Test
    fun whenSendNumberLengthIsElevenAndStartedWithSevenReturnTrue() {
        val result = testBody("79084562312")
        assertTrue(result!!)
    }

    @Test
    fun whenSendNumberLengthIsElevenAndStartedWithSevenAndSpecialSymbolsReturnTrue() {
        val result = testBody("+7(909)456-11-23")
        assertTrue(result!!)
    }

    private fun testBody(phoneNumber: String?): @NotNull Boolean? {
        val phone = PhoneNumberDto().phone(phoneNumber);
        val request = HttpEntity(phone);
        val responseEntity = restTemplate.exchange<SendingResultDto>(BASE_URL, POST, request, SendingResultDto::class)
        assertEquals(OK, responseEntity.statusCode)
        val body = responseEntity.body
        assertNotNull(body)
        val result = body?.isSuccessful
        assertNotNull(result)
        return result
    }
}