package dev.matzat.anagram

import dev.matzat.anagram.api.configureMonitoring
import dev.matzat.anagram.api.configurePlugins
import dev.matzat.anagram.api.configureRouting
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configurePlugins()
    configureMonitoring()
    configureRouting()
}
