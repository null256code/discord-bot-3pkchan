package spkchan.application.usecases.cooking

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent
import discord4j.core.`object`.component.ActionRow
import discord4j.core.`object`.component.Button
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import spkchan.adapter.listeners.MainCommand
import spkchan.application.usecases.ApplicationCommandInteractionUseCase
import spkchan.external.apis.rakuten.queries.FetchDailyMenuQuery

@Component
class SearchRecipeUseCase(
    private val fetchDailyMenuQuery: FetchDailyMenuQuery,
) : ApplicationCommandInteractionUseCase {
    override val mainCommand = MainCommand.Cook

    override fun handle(event: ApplicationCommandInteractionEvent): Mono<*> {
        val recipes = fetchDailyMenuQuery.handle().recipes
        val recipeRanking = recipes.joinToString("\n\n") {
            """
${it.rank}位 ${it.title}
【材料】${it.ingredientsDescription.replace(Regex("[\r\n]]"), " ")}
【説明】${it.recipeDescription}
            """.trimIndent()
        }
        val linkButtons = recipes.map { Button.link(it.recipeUrl, "${it.rank}位") }

        // 応答に3秒以上かかる場合はdeferReplyを使わないとタイムアウトする
        // https://docs.discord4j.com/interactions/application-commands#responding-to-commands
        return event.deferReply().then(
            event.createFollowup(
                """
今日の献立ランキングです
```
$recipeRanking
```
                """.trimIndent(),
            ).withComponents(ActionRow.of(linkButtons)),
        )
    }
}
