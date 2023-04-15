package spkchan.application.usecases

import discord4j.core.event.domain.interaction.ButtonInteractionEvent
import reactor.core.publisher.Mono
import spkchan.adapter.listeners.BotButton

interface ButtonInteractionUseCase {
    val button: BotButton
    fun handle(event: ButtonInteractionEvent): Mono<*>
}
