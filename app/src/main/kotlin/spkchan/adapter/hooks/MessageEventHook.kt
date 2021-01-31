package spkchan.adapter.hooks

import discord4j.core.`object`.entity.Message
import discord4j.core.event.domain.message.MessageCreateEvent

object MessageEventHook : EventHook<MessageCreateEvent> {

    override fun handle(event: MessageCreateEvent) {
        val message = event.message
        val text = message.content
        when {
            text.startsWith("cook") -> useCookingBot(message)
            text.contains("テスト") -> greeting(message)
            else -> return // do nothing
        }
    }

    private fun useCookingBot(message: Message) {
        val channel = message.channel.block()
        channel.createMessage("""
            白菜と鶏肉のトロっと煮
            https://recipe.rakuten.co.jp/recipe/1730000043/
        """.trimIndent()).block()
    }

    private fun greeting(message: Message) {
        val channel = message.channel.block()
        channel.createMessage("レスポンス").block()
    }
}