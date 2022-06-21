package ru.any.auth

import com.ninjasquad.springmockk.MockkBean
import lombok.extern.slf4j.Slf4j
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import ru.any.auth.config.AnyAuthenticateServiceTestConfig
import ru.any.auth.feign.RedSmsFeignClient

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [AuthApplication::class])
@ActiveProfiles("test")
abstract class AbstractIntegrationTest : AnyAuthenticateServiceTestConfig() {

    @MockkBean
    protected lateinit var redSmsFeignClient: RedSmsFeignClient
}