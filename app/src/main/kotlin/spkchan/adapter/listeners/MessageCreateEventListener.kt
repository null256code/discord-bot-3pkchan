package spkchan.adapter.listeners

import discord4j.core.event.domain.message.MessageCreateEvent
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import spkchan.application.usecases.MessageCreateUseCase

@Service
class MessageCreateEventListener(
    private val useCases: List<MessageCreateUseCase>,
) : EventListener<MessageCreateEvent> {

    override val eventType: Class<MessageCreateEvent> get() = MessageCreateEvent::class.java

    override fun execute(event: MessageCreateEvent): Mono<Void> {
        return event.message.let {
            useCases.firstOrNull { uc -> uc.isCalled(it) }?.handle(it)?.then() ?: Mono.empty()
        }
    }
}
