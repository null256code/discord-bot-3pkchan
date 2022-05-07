package spkchan

import discord4j.core.DiscordClientBuilder
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.Event
import spkchan.adapter.listeners.EventListener
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BotClientConfig(
    @Value("\${botconfig.discord.token}")
    val token: String
) {
    @Bean
    fun <T : Event> gatewayDiscordClient(eventListeners: List<EventListener<T>>): GatewayDiscordClient {
        val client = DiscordClientBuilder.create(token).build().login().block()!!

        eventListeners.forEach { listener ->
            client.on(listener.eventType)
                .flatMap { listener.execute(it) }
                .onErrorResume { listener.handleError(it) }
                .subscribe()
        }
        return client
    }
}