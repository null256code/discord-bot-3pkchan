package spkchan.application.usecases.finance

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import spkchan.adapter.listeners.MainCommand
import spkchan.application.usecases.ApplicationCommandInteractionUseCase
import spkchan.domain.models.ExternalService
import spkchan.domain.models.finance.Income
import spkchan.domain.models.finance.Payment
import spkchan.domain.models.finance.PaymentCategory
import spkchan.external.apis.zaim.ZaimApiClient
import spkchan.persistence.repositories.AccountRepository
import spkchan.persistence.repositories.ConnectedServiceRepository
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

@Component
class ReportWeeklyBalanceUseCase(
    private val zaimApiClient: ZaimApiClient,
    private val accountRepository: AccountRepository,
    private val connectedServiceRepository: ConnectedServiceRepository,
) : ApplicationCommandInteractionUseCase {
    override val mainCommand = MainCommand.Kakeibo
    override val subCommand = MainCommand.Kakeibo.Report
    override fun handle(event: ApplicationCommandInteractionEvent): Mono<*> {
        val discordMember = event.interaction.member.get()
        val account = accountRepository.findAccount(discordMember.id.asLong())
            ?: return event.reply("アカウントが見つかりません")
        val zaim = connectedServiceRepository.findConnectedServices(account).firstOrNull {
            it.serviceType == ExternalService.Zaim
        } ?: return event.reply("zaimの登録情報が見つかりません")

        val records = zaimApiClient.fetchWeeklyBalance(zaim.accessToken)
        val incomes = records.filterIsInstance(Income::class.java)
        val payments = records.filterIsInstance(Payment::class.java)

        val lastMonday = LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.MONDAY))
        val lastSunday = lastMonday.plusWeeks(1).minusDays(1)

        val report = """
            集計時期　： ${lastMonday.format(DateTimeFormatter.ISO_DATE)} ～ ${lastSunday.format(DateTimeFormatter.ISO_DATE)}
            取引数　　： 収入 ${incomes.size}件 / 支払 ${payments.size}件 / 合計 ${records.size}件
            収入合計額： ${incomes.sumOf { it.amount }}円
            支払合計額： ${payments.sumOf { it.amount }}円
            ***
            「未分類」の支払レコード： ${payments.count { it.subGenre == PaymentCategory.Other.Uncategorized }}件
        """.trimIndent()

        return event.reply(report)
    }
}
