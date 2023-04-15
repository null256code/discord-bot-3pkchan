package spkchan.application.usecases.finance

import discord4j.core.event.domain.interaction.ButtonInteractionEvent
import discord4j.core.`object`.component.ActionRow
import discord4j.core.`object`.component.TextInput
import discord4j.core.spec.InteractionPresentModalSpec
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import spkchan.adapter.listeners.BotButton
import spkchan.adapter.listeners.BotModal
import spkchan.application.usecases.ButtonInteractionUseCase

@Component
class CreateTokenInputModalUseCase() : ButtonInteractionUseCase {
    override val button = BotButton.CREATE_TOKEN_INPUT_MODAL_BUTTON
    override fun handle(event: ButtonInteractionEvent): Mono<*> {
        val modal = InteractionPresentModalSpec.builder()
            .customId(BotModal.TOKEN_INPUT_MODAL.name)
            .title("test")
            .addComponent(
                ActionRow.of(
                    TextInput
                        .small(BotModal.TOKEN_INPUT_MODAL.textInputId, "zaimのトークン", "abc123hoge...")
                        .required(),
                ),
            )
            .build()
        return event.presentModal(modal)
    }
}
