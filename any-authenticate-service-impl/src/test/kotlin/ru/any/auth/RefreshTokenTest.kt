package ru.any.auth

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import ru.any.auth.dto.JwtResponseDto
import ru.any.auth.dto.TokensDto
import ru.any.auth.service.TokenGenerator
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class RefreshTokenTest : AbstractIntegrationTest() {

    companion object {
        const val BASE_ACCESS_URL = "/api/v1/refresh_access_token"
        const val BASE_REFRESH_URL = "/api/v1/refresh_refresh_token"
    }

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var tokenGenerator: TokenGenerator

    @Test
    fun returnErrorMessageIfRefreshTokenIsNotValid() {
        val token = TokensDto().refreshToken("skjdfhlksdf").refreshToken(";sjdhfvlksdhf")
        val jwtResponseDto = sendRequestToRefreshTokensAndGetResponse(token, BASE_ACCESS_URL)
        assertNull(jwtResponseDto.accessToken)
        assertNull(jwtResponseDto.refreshToken)
        assertNotNull(jwtResponseDto.errorMessage)
        assertEquals("Invalid tokens", jwtResponseDto.errorMessage)
    }

    @Test
    fun returnErrorIfTokensSubjectsIsDifferent() {
        val accessToken = tokenGenerator.generateAccessToken("89112364578")
        val refreshToken = tokenGenerator.generateRefreshToken("89994576545")
        val token = TokensDto().accessToken(accessToken).refreshToken(refreshToken)
        val jwtResponseDto = sendRequestToRefreshTokensAndGetResponse(token, BASE_ACCESS_URL)
        assertNull(jwtResponseDto.accessToken)
        assertNull(jwtResponseDto.refreshToken)
        assertNotNull(jwtResponseDto.errorMessage)
        assertEquals("Invalid tokens", jwtResponseDto.errorMessage)
    }

    @Test
    fun returnNewAccessToken() {
        val accessToken = tokenGenerator.generateAccessToken("89112364578")
        val refreshToken = tokenGenerator.generateRefreshToken("89112364578")
        val token = TokensDto().accessToken(accessToken).refreshToken(refreshToken)
        val jwtResponseDto = sendRequestToRefreshTokensAndGetResponse(token, BASE_ACCESS_URL)
        assertNull(jwtResponseDto.errorMessage)
        assertNotNull(jwtResponseDto.accessToken)
        assertNotNull(jwtResponseDto.refreshToken)
    }

    @Test
    fun returnNewRefreshToken() {
        val accessToken = tokenGenerator.generateAccessToken("89112364578")
        val refreshToken = tokenGenerator.generateRefreshToken("89112364578")
        val token = TokensDto().accessToken(accessToken).refreshToken(refreshToken)
        val jwtResponseDto = sendRequestToRefreshTokensAndGetResponse(token, BASE_REFRESH_URL)
        assertNull(jwtResponseDto.errorMessage)
        assertNotNull(jwtResponseDto.accessToken)
        assertNotNull(jwtResponseDto.refreshToken)
    }

    private fun sendRequestToRefreshTokensAndGetResponse(token: TokensDto, url: String): JwtResponseDto {
        val request = HttpEntity(token)
        val response =
            restTemplate.exchange<JwtResponseDto>(url, HttpMethod.POST, request, JwtResponseDto::class)
        assertEquals(HttpStatus.OK, response.statusCode)
        val jwtResponseDto = response.body
        assertNotNull(jwtResponseDto)
        return jwtResponseDto
    }
}