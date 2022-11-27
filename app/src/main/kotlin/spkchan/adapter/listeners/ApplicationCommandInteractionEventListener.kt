package spkchan.adapter.listeners

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import spkchan.application.usecases.ApplicationCommandInteractionUseCase

@Service
class ApplicationCommandInteractionEventListener(
        private val useCases: List<ApplicationCommandInteractionUseCase>
) : EventListener<ApplicationCommandInteractionEvent> {
    override val eventType: Class<ApplicationCommandInteractionEvent>
        get() = ApplicationCommandInteractionEvent::class.java

    override fun execute(event: ApplicationCommandInteractionEvent): Mono<Void> {
        return useCases.firstOrNull { it.isCalled(event.interaction.commandInteraction.get())}?.handle(event) ?: Mono.empty()
    }

}