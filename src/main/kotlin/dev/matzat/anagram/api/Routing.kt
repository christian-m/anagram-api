package dev.matzat.anagram.api

import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureRouting() =
    routing {
        root()
    }

fun Route.root() =
    route("/") {
        get {
            call.respondText("Welcome to the Anagram API")
        }
    }
