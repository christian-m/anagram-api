package dev.matzat.anagram

import dev.matzat.anagram.api.configureMonitoring
import dev.matzat.anagram.api.configurePlugins
import dev.matzat.anagram.api.configureRouting
import dev.matzat.anagram.api.configureSwagger
import dev.matzat.anagram.persistence.config.configureDatabase
import dev.matzat.anagram.persistence.dao.AnagramDao
import dev.matzat.anagram.service.AnagramService
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.kodein.di.ktor.di

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureDi()
    configureDatabase()
    configurePlugins()
    configureMonitoring()
    configureSwagger()
    configureRouting()
}

fun Application.configureDi() {
    di {
        bindSingleton<AnagramDao> { AnagramDao() }
        bindSingleton<AnagramService> { AnagramService(instance<AnagramDao>()) }
    }
}
