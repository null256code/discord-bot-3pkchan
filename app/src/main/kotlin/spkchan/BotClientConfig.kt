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
    @Value("\${botconfig.discord.application_id}") val applicationId: Long,
    @Value("\${botconfig.discord.server_id}") val serverId: Long,
) {
    @Bean
    fun <T : Event> gatewayDiscordClient(
        eventListeners: List<EventListener<T>>,
        commandUseCases: List<ApplicationCommandInteractionUseCase>,
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
                var isModified = false
                getGuildApplicationCommands(applicationId, serverId).subscribe {
                    if (it.name() == name) {
                        modifyGuildApplicationCommand(applicationId, serverId, it.id().asLong(), request).subscribe()
                        isModified = true
                    }
                }
                if (!isModified) {
                    createGuildApplicationCommand(applicationId, serverId, request).subscribe()
                }
            }
            // コマンドの削除
            getGuildApplicationCommands(applicationId, serverId)
                .filter { commandUseCases.all { uc -> uc.commandName != it.name() } }
                .subscribe { deleteGuildApplicationCommand(applicationId, serverId, it.id().asLong()).subscribe() }
        }

        return client
    }
}
