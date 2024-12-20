package dev.matzat.anagram

import dev.matzat.anagram.persistence.config.setupDatabaseConnection
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.config.mergeWith
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.test.TestResult
import org.testcontainers.containers.PostgreSQLContainer

@Suppress("UtilityClassWithPublicConstructor")
abstract class DbTestBase {
    companion object {
        val container = PostgreSQLContainer("postgres:17").also { it.start() }

        internal fun setupDatabaseConnection() =
            setupDatabaseConnection(container.host, container.firstMappedPort.toString(), container.databaseName, container.username, container.password)
    }

    internal fun withTestApplication(block: suspend ApplicationTestBuilder.() -> Unit): TestResult =
        testApplication {
            environment {
                config =
                    ApplicationConfig("application.yaml")
                        .mergeWith(MapApplicationConfig("ktor.database.host" to container.host))
                        .mergeWith(MapApplicationConfig("ktor.database.port" to container.firstMappedPort.toString()))
                        .mergeWith(MapApplicationConfig("ktor.database.name" to container.databaseName))
                        .mergeWith(MapApplicationConfig("ktor.database.user" to container.username))
                        .mergeWith(MapApplicationConfig("ktor.database.password" to container.password))
            }
            block
        }
}
