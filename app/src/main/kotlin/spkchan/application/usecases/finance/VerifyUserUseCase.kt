package spkchan.application.usecases.finance

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import spkchan.adapter.listeners.MainCommand
import spkchan.application.usecases.ApplicationCommandInteractionUseCase
import spkchan.domain.models.ExternalService
import spkchan.external.apis.zaim.ZaimApiClient
import spkchan.persistence.repositories.AccountRepository
import spkchan.persistence.repositories.ConnectedServiceRepository

@Component
class VerifyUserUseCase(
    private val zaimApiClient: ZaimApiClient,
    private val accountRepository: AccountRepository,
    private val connectedServiceRepository: ConnectedServiceRepository,
) : ApplicationCommandInteractionUseCase {
    override val mainCommand = MainCommand.Kakeibo
    override val subCommand = MainCommand.Kakeibo.VerifyUser

    override fun handle(event: ApplicationCommandInteractionEvent): Mono<*> {
        val discordMember = event.interaction.member.get()
        val account = accountRepository.findAccount(discordMember.id.asLong())
            ?: return event.reply("アカウントが見つかりません")
        val zaim = connectedServiceRepository.findConnectedServices(account).filter {
            it.serviceType == ExternalService.Zaim
        }.firstOrNull() ?: return event.reply("zaimの登録情報が見つかりません")

        return if (zaimApiClient.verifyUser(zaim.accessToken)) {
            event.reply("zaimの認証が有効なことが確認できました")
        } else {
            event.reply("zaimの認証が有効ではありません")
        }
    }
}
