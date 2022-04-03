package spkchan.application.usecases

import discord4j.core.`object`.entity.Message
import reactor.core.publisher.Mono

interface MessageCreateUseCase {
    fun isCalled(message: Message): Boolean
    fun handle(message: Message): Mono<Message>
}