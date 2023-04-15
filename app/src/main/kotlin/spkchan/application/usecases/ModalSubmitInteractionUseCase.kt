package spkchan.application.usecases

import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent
import reactor.core.publisher.Mono
import spkchan.adapter.listeners.BotModal

interface ModalSubmitInteractionUseCase {
    val modal: BotModal
    fun handle(event: ModalSubmitInteractionEvent): Mono<*>
}
