package spkchan.application.usecases.finance

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent
import discord4j.core.`object`.component.ActionRow
import discord4j.core.`object`.component.Button
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import spkchan.adapter.listeners.MainCommand
import spkchan.application.usecases.ApplicationCommandInteractionUseCase
import spkchan.domain.models.UnsavedDiscordAccount
import spkchan.external.apis.zaim.ZaimApiClient
import spkchan.persistence.repositories.AccountRepository
import spkchan.persistence.repositories.ConnectedServiceRepository

@Component
class AuthorizeFinanceServiceUseCase(
    private val zaimApiClient: ZaimApiClient,
    private val accountRepository: AccountRepository,
    private val connectedServiceRepository: ConnectedServiceRepository,
) : ApplicationCommandInteractionUseCase {

    override val mainCommand = MainCommand.Kakeibo
    override val subCommand = MainCommand.Kakeibo.SignUp

    override fun handle(event: ApplicationCommandInteractionEvent): Mono<*> {
        val requestToken = zaimApiClient.fetchRequestToken()

        val discordMember = event.interaction.member.get()
        val fetchedAccount = accountRepository.fetchAccount(discordMember.id.asLong())
        val discordAccount = fetchedAccount ?: discordMember.run {
            val unsavedAccount = UnsavedDiscordAccount(id.asLong(), username, discriminator)
            accountRepository.saveAccount(unsavedAccount)
        }

        connectedServiceRepository.saveRequestToken(discordAccount, "zaim", requestToken.tokenSecret)

        val url = zaimApiClient.generateAuthorizationUrl(requestToken)
        return if (false) {
            // TODO: 既に認証済みのパターンも考慮に入れる
            event.reply("以下のリンクからZaimのアカウント認証を行ってください。")
        } else {
            event
                .reply(
                    """
                    以下の操作をお願いします
                    ① リンクボタンからZaimのアカウントを認証
                    ② ①の最後に表示されるトークンを「トークンを登録」ボタンで入力
                    """.trimIndent(),
                )
                .withComponents(
                    ActionRow.of(Button.link(url, "Zaimアカウント認証")),
                    ActionRow.of(Button.primary("XXX_TODO_REGISTER_BUTTON_XXX", "トークンを登録")),
                )
        }
    }
}
