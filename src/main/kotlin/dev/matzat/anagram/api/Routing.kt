package dev.matzat.anagram.api

import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureRouting() =
    routing {
        root()
    }

fun Route.root() =
    route("/") {
        get(
            {
                description = "Greeting Endpoint."
                response {
                    HttpStatusCode.OK to {
                        description = "Successful Request"
                        body<String> { description = "the response" }
                    }
                }
            },
        ) {
            call.respondText("Welcome to the Anagram API")
        }
    }
