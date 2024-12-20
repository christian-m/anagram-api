package dev.matzat.anagram.api

import dev.matzat.anagram.api.model.ComparisonResponse
import dev.matzat.anagram.api.model.HistoryResponse
import dev.matzat.anagram.service.AnagramService
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.swagger.v3.oas.models.media.Schema
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

private val logger = KotlinLogging.logger {}

fun Application.configureRouting() =
    routing {
        compare()
        history()
    }

fun Route.compare() =
    route("/compare") {
        post(
            {
                description = "Compares a text against another candidate and returns if those are an anagram of each other."
                request {
                    body(
                        Schema<Any>().apply {
                            types = setOf("object")
                            properties =
                                mapOf(
                                    "text" to
                                        Schema<String>().apply {
                                            types = setOf("string")
                                            description = "a text that should be compared against a candidate text"
                                        },
                                    "candidate" to
                                        Schema<String>().apply {
                                            types = setOf("string")
                                            description = "a candidate text that should be compared against a text"
                                        },
                                )
                        },
                    ) {
                        mediaTypes(ContentType.Application.FormUrlEncoded)
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        description = "The comparison was successful"
                        body<ComparisonResponse> {
                            description = "The result of the comparison"
                        }
                    }
                    HttpStatusCode.BadRequest to {
                        description = "Invalid input data was provided"
                    }
                }
            },
        ) {
            val service by call.closestDI().instance<AnagramService>()
            try {
                val formParameters = call.receiveParameters()
                val text = formParameters["text"].orEmpty()
                val candidate = formParameters["candidate"].orEmpty()
                logger.info { "got comparison request: [$text] [$candidate]" }
                val result = service.compare(text, candidate)
                call.respond(HttpStatusCode.OK, ComparisonResponse(text, candidate, result))
            } catch (ex: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                logger.debug(ex) { "Bad Request" }
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest)
                logger.debug(ex) { "Bad Request" }
            }
        }
    }

fun Route.history() =
    route("/history") {
        post(
            {
                description = "Searches the input history to find all anagrams of this text that have been entered until now."
                request {
                    body(
                        Schema<Any>().apply {
                            types = setOf("object")
                            properties =
                                mapOf(
                                    "text" to
                                        Schema<String>().apply {
                                            types = setOf("string")
                                            description = "a text that should be compared against the history of inputs"
                                        },
                                )
                        },
                    ) {
                        mediaTypes(ContentType.Application.FormUrlEncoded)
                    }
                }

                response {
                    HttpStatusCode.OK to {
                        description = "The history search was successful"
                        body<HistoryResponse> {
                            mediaTypes(ContentType.Application.Json)
                            description = "All found anagrams of this text in history"
                        }
                    }
                    HttpStatusCode.BadRequest to {
                        description = "Invalid input data was provided"
                    }
                }
            },
        ) {
            val service by call.closestDI().instance<AnagramService>()
            try {
                val formParameters = call.receiveParameters()
                val textToFind = formParameters["text"].orEmpty()
                logger.info { "got history search request: [$textToFind]" }
                val result = service.findInHistory(textToFind)
                call.respond(HttpStatusCode.OK, HistoryResponse(textToFind, result))
            } catch (ex: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                logger.debug(ex) { "Bad Request" }
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest)
                logger.debug(ex) { "Bad Request" }
            }
        }
    }
