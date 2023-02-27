package spkchan

import discord4j.core.DiscordClientBuilder
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.Event
import discord4j.discordjson.json.ApplicationCommandData
import discord4j.discordjson.json.ImmutableApplicationCommandRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import spkchan.adapter.listeners.EventListener
import spkchan.application.usecases.ApplicationCommandInteractionUseCase

@Configuration
class BotClientConfig(
    @Value("\${botconfig.discord.token}") val token: String,
    @Value("\${botconfig.discord.application_id}") val applicationId: Long,
    @Value("\${botconfig.discord.server_id:0}") val serverId: Long,
    @Value("\${botconfig.discord.development_mode:true}") val isDevelopmentMode: Boolean,
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

        if (isDevelopmentMode) {
            require(serverId != 0L) { "development_modeの場合はserver_idが必要です" }
        }

        // slash command の登録
        client.restClient.applicationService.run {
            if (isDevelopmentMode) {
                ApplyCommandFunctions(
                    { getGuildApplicationCommands(applicationId, serverId) },
                    { createGuildApplicationCommand(applicationId, serverId, it) },
                    { id, request -> modifyGuildApplicationCommand(applicationId, serverId, id, request) },
                    { deleteGuildApplicationCommand(applicationId, serverId, it) },
                )
            } else {
                ApplyCommandFunctions(
                    { getGlobalApplicationCommands(applicationId) },
                    { createGlobalApplicationCommand(applicationId, it) },
                    { id, request -> modifyGlobalApplicationCommand(applicationId, id, request) },
                    { deleteGlobalApplicationCommand(applicationId, it) },
                )
            }.run {
                // コマンドの更新・登録
                commandUseCases.map { it.commandName to it.commandRequest }.forEach { (name, request) ->
                    var isModified = false
                    findCommandsAction().subscribe {
                        if (it.name() == name) {
                            modifyAction(it.id().asLong(), request).subscribe()
                            isModified = true
                        }
                    }
                    if (!isModified) {
                        createAction(request).subscribe()
                    }
                }
                // コマンドの削除
                findCommandsAction()
                    .filter { commandUseCases.all { uc -> uc.commandName != it.name() } }
                    .subscribe { deleteAction(it.id().asLong()).subscribe() }
            }
        }
        return client
    }
}

data class ApplyCommandFunctions(
    val findCommandsAction: () -> Flux<ApplicationCommandData>,
    val createAction: (ImmutableApplicationCommandRequest) -> Mono<ApplicationCommandData>,
    val modifyAction: (commandId: Long, ImmutableApplicationCommandRequest) -> Mono<ApplicationCommandData>,
    val deleteAction: (commandId: Long) -> Mono<Void>,
)
