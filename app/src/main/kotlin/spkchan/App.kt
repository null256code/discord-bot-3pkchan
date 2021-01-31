/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package spkchan

import discord4j.core.DiscordClientBuilder
import discord4j.core.event.domain.message.MessageCreateEvent
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import spkchan.adapter.hooks.MessageEventHook

// https://github.com/Discord4J/Discord4J/blob/master/README.md
fun main() {

    startKoin {
        environmentProperties()
        modules(listOf(module))
    }
    val config by inject(Config::class.java)

    val client = DiscordClientBuilder.create(config.discordToken).build()
    val gateway = client.login().block()
    gateway.on(MessageCreateEvent::class.java).subscribe { MessageEventHook.handle(it) }

    gateway.onDisconnect().block()
}

val module = module {
    single { Config(getProperty("DISCORD_TOKEN")) }
}

data class Config(val discordToken: String)