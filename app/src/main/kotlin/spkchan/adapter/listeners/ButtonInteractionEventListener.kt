package spkchan.adapter.listeners

import discord4j.core.event.domain.interaction.ButtonInteractionEvent
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import spkchan.application.usecases.ButtonInteractionUseCase

@Service
class ButtonInteractionEventListener(
    private val useCases: List<ButtonInteractionUseCase>,
) : EventListener<ButtonInteractionEvent> {
    override val eventType: Class<ButtonInteractionEvent>
        get() = ButtonInteractionEvent::class.java

    override fun execute(event: ButtonInteractionEvent): Mono<Void> {
        return useCases.firstOrNull { it.button.name == event.customId }?.handle(event)?.then() ?: Mono.empty()
    }
}
