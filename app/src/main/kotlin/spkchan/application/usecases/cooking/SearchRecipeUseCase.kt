package spkchan.application.usecases.cooking

import discord4j.core.`object`.entity.Message
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import spkchan.application.usecases.MessageCreateUseCase

@Component
class SearchRecipeUseCase : MessageCreateUseCase {

    override fun isCalled(message: Message): Boolean {
        return message.content.contains("cook")
    }

    override fun handle(message: Message): Mono<Message> {
        return message.channel
            .flatMap {
                it.createMessage(
                    """
            白菜と鶏肉のトロっと煮
            https://recipe.rakuten.co.jp/recipe/1730000043/
            """.trimIndent()
                )
            }
    }
}