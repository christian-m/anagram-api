package dev.matzat.anagram.persistence.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import javax.sql.DataSource

fun Application.configureDatabase() {
    val dbHost = environment.config.property("ktor.database.host").getString()
    val dbPort = environment.config.property("ktor.database.port").getString()
    val dbName = environment.config.property("ktor.database.name").getString()
    val dbUser = environment.config.property("ktor.database.user").getString()
    val dbPassword = environment.config.property("ktor.database.password").getString()

    setupDatabaseConnection(dbHost, dbPort, dbName, dbUser, dbPassword)
}

internal fun setupDatabaseConnection(
    dbHost: String,
    dbPort: String,
    dbName: String,
    dbUser: String,
    dbPassword: String,
) {
    val dataSource = dataSource(dbHost, dbPort, dbName, dbUser, dbPassword)
    Flyway.configure().dataSource(dataSource).load().also {
        it.info()
        it.migrate()
    }
    Database.connect(dataSource)
}

private fun dataSource(
    dbHost: String,
    dbPort: String,
    dbName: String,
    dbUser: String,
    dbPassword: String,
): DataSource =
    HikariDataSource(
        HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://$dbHost:$dbPort/$dbName"
            driverClassName = "org.postgresql.Driver"
            username = dbUser
            password = dbPassword
            maximumPoolSize = 3
            // as of version 0.46.0, if these options are set here, they do not need to be duplicated in DatabaseConfig
            isReadOnly = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        },
    )
