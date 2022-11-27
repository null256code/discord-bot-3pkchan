package spkchan

import discord4j.core.DiscordClientBuilder
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.Event
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import spkchan.adapter.listeners.EventListener
import spkchan.application.usecases.ApplicationCommandInteractionUseCase

@Configuration
class BotClientConfig(
    @Value("\${botconfig.discord.token}") val token: String,
    @Value("\${botconfig.discord.application_id}") val applicationId: Long
) {
    @Bean
    fun <T : Event> gatewayDiscordClient(
        eventListeners: List<EventListener<T>>,
        commandUseCases: List<ApplicationCommandInteractionUseCase>
    ): GatewayDiscordClient {
        val client = DiscordClientBuilder.create(token).build().login().block()!!

        eventListeners.forEach { listener ->
            client.on(listener.eventType)
                .flatMap { listener.execute(it) }
                .onErrorResume { listener.handleError(it) }
                .subscribe()
        }

        client.restClient.applicationService.run {
            // コマンドの更新・登録
            commandUseCases.map { it.commandName to it.commandRequest }.forEach { (name, request) ->
                getGlobalApplicationCommands(applicationId).subscribe {
                    if (it.name() == name) {
                        modifyGlobalApplicationCommand(applicationId, it.id().asLong(), request).subscribe()
                    } else {
                        createGlobalApplicationCommand(applicationId, request).subscribe()
                    }
                }
            }
            // コマンドの削除
            getGlobalApplicationCommands(applicationId)
                .filter { commandUseCases.all { uc -> uc.commandName != it.name() } }
                .subscribe { deleteGlobalApplicationCommand(applicationId, it.id().asLong()).subscribe() }
        }

        return client
    }
}