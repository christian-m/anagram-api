package dev.matzat.anagram.api

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import io.ktor.server.response.respondText
import org.slf4j.event.Level

private val logger = KotlinLogging.logger {}

fun Application.configurePlugins() {
    install(CallLogging) {
        level = Level.INFO
        format { call ->
            val status = call.response.status()
            val uri = call.request.uri
            val httpMethod = call.request.httpMethod.value
            val userAgent = call.request.headers["User-Agent"]
            "$httpMethod $uri $status - $userAgent"
        }
    }
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: An unexpected error occurs", status = HttpStatusCode.InternalServerError)
            logger.error(cause) { "An unexpected error occurred" }
        }
    }
}
