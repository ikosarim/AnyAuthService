package ru.any.auth.config

import org.testcontainers.containers.PostgreSQLContainer

class MyPostgreSQLContainer(imageName: String) : PostgreSQLContainer<MyPostgreSQLContainer>(imageName) {
}