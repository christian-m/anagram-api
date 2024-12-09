package dev.matzat.anagram.api

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureSwagger() {
    install(SwaggerUI) {
        info {
            title = "Compare Anagram API"
            version = "v1.0.0"
            description = "REST API to compare texts against each other if they are anagrams."
        }
    }
    routing {
        route("/openapi.json") {
            openApiSpec()
        }
        // Create a route for the swagger-ui using the openapi-spec at "/api.json".
        route("/swagger-ui") {
            swaggerUI("/openapi.json")
        }
    }
}
