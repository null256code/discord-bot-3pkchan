package spkchan.adapter.listeners

import discord4j.core.event.domain.Event
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono

interface EventListener<T : Event> {

    companion object {
        private val logger = LoggerFactory.getLogger(EventListener::class.java)
    }

    val eventType: Class<T>
    fun execute(event: T): Mono<*>
    fun handleError(error: Throwable): Mono<Void> {
        // logger.error(error.toString())
        logger.error(error.stackTraceToString())
        return Mono.empty()
    }
}
