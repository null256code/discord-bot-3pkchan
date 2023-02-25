package spkchan.application.usecases

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent
import discord4j.core.`object`.command.ApplicationCommandInteraction
import discord4j.discordjson.json.ImmutableApplicationCommandRequest
import reactor.core.publisher.Mono

interface ApplicationCommandInteractionUseCase {

    val commandName: String
    val commandRequest: ImmutableApplicationCommandRequest
    fun isCalled(command: ApplicationCommandInteraction): Boolean = command.name.get() == commandName
    fun handle(event: ApplicationCommandInteractionEvent): Mono<*>

}