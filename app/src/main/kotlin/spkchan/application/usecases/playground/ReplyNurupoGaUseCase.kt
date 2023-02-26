package spkchan.application.usecases.playground

import discord4j.core.`object`.entity.Message
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import spkchan.application.usecases.MessageCreateUseCase

@Component
class ReplyNurupoGaUseCase : MessageCreateUseCase {
    override fun isCalled(message: Message) = message.content.contains(Regex("(ぬるぽ|ヌルポ|ﾇﾙﾎﾟ)"))
    override fun handle(message: Message): Mono<Message> = message.channel.flatMap {
        val replyMessage = sequenceOf("ガッ", "■━⊂( ･∀･) 彡 ｶﾞｯ☆`Д´)ﾉ").shuffled().first()
        it.createMessage(replyMessage)
    }
}
