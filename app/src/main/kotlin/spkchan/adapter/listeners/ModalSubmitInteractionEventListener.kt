package spkchan.adapter.listeners

import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import spkchan.application.usecases.ModalSubmitInteractionUseCase

@Service
class ModalSubmitInteractionEventListener(
    private val useCases: List<ModalSubmitInteractionUseCase>,
) : EventListener<ModalSubmitInteractionEvent> {
    override val eventType: Class<ModalSubmitInteractionEvent>
        get() = ModalSubmitInteractionEvent::class.java

    override fun execute(event: ModalSubmitInteractionEvent): Mono<Void> {
        return useCases.firstOrNull { it.modal.name == event.customId }?.handle(event)?.then() ?: Mono.empty()
    }
}
