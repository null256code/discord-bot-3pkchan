package spkchan.application.usecases

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent
import discord4j.core.`object`.command.ApplicationCommandInteraction
import discord4j.core.`object`.command.ApplicationCommandOption
import reactor.core.publisher.Mono
import spkchan.adapter.listeners.MainCommand
import spkchan.adapter.listeners.SubCommand

interface ApplicationCommandInteractionUseCase {

    val mainCommand: MainCommand
    val subCommand: SubCommand? get() = null
    fun isCalled(command: ApplicationCommandInteraction): Boolean {
        val mailCommandCalled = command.name.get() == mainCommand.name
        if (subCommand == null) {
            return mailCommandCalled
        }
        val subCommandOptions = command.options.filter { it.type == ApplicationCommandOption.Type.SUB_COMMAND }
        return mailCommandCalled && subCommandOptions.any { it.name == subCommand?.name }
    }
    fun handle(event: ApplicationCommandInteractionEvent): Mono<*>
}
