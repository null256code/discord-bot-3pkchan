package spkchan.adapter.listeners

import discord4j.core.event.domain.Event
import reactor.core.publisher.Mono

interface EventListener<T : Event> {

    val eventType: Class<T>
    fun execute(event: T): Mono<Void>
    fun handleError(error: Throwable): Mono<Void> {
        return Mono.empty()
    }
}