package ru.any.auth.config

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@DirtiesContext
open class AnyAuthenticateServiceTestConfig {

    companion object {

        @Container
        val container: MyPostgreSQLContainer = MyPostgreSQLContainer("postgres:12")
            .withUsername("any_postgres")
            .withPassword("any_postgres")
            .withDatabaseName("any_db")
            .withInitScript("database-init.sql")
            .withUrlParam("currentSchema", "any_auth")

        @JvmStatic
        @DynamicPropertySource
        fun registerPgProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { container.jdbcUrl }
            registry.add("spring.datasource.username") { container.username }
            registry.add("spring.datasource.password") { container.password }
            registry.add("spring.liquibase.change-log") { "classpath:/db/changelog/database_any_auth_changelog.xml" }
        }
    }
}