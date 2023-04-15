package spkchan.application.usecases.finance

import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent
import discord4j.core.`object`.component.TextInput
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import spkchan.adapter.listeners.BotModal
import spkchan.application.usecases.ModalSubmitInteractionUseCase
import spkchan.domain.models.ExternalService
import spkchan.domain.models.UnsavedAuthenticatedService
import spkchan.external.apis.zaim.ZaimApiClient
import spkchan.persistence.repositories.AccountRepository
import spkchan.persistence.repositories.ConnectedServiceRepository

@Component
class SaveAccessTokenForFinanceServiceUseCase(
    private val zaimApiClient: ZaimApiClient,
    private val accountRepository: AccountRepository,
    private val connectedServiceRepository: ConnectedServiceRepository,
) : ModalSubmitInteractionUseCase {

    override val modal = BotModal.TOKEN_INPUT_MODAL

    override fun handle(event: ModalSubmitInteractionEvent): Mono<*> {
        val oauthVerifierInput = event.getComponents(TextInput::class.java)
            .firstOrNull { it.customId == BotModal.TOKEN_INPUT_MODAL.textInputId }
            ?: return event.reply("入力されたフォームを検知できませんでした")

        val discordMember = event.interaction.member.get()
        val account = accountRepository.findAccount(discordMember.id.asLong())
            ?: return event.reply("アカウントが見つかりません")
        val requestToken = connectedServiceRepository.findRequestToken(account, ExternalService.Zaim)
            ?: return event.reply("保存された認証が見つかりません。もう一度signupを実行すると解決するかもしれません")

        if (oauthVerifierInput.value.isEmpty) {
            return event.reply("フォームの値が空です")
        }
        val accessToken = zaimApiClient.fetchAccessToken(requestToken, oauthVerifierInput.value.get())
        val service = UnsavedAuthenticatedService(ExternalService.Zaim, accessToken)
        connectedServiceRepository.saveAccessToken(account, service)

        return event.reply("トークンの保存完了しました")
    }
}
