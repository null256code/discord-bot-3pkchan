package spkchan.application.usecases.cooking

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent
import discord4j.core.`object`.command.ApplicationCommandOption
import discord4j.core.`object`.entity.Message
import discord4j.discordjson.json.ApplicationCommandOptionData
import discord4j.discordjson.json.ApplicationCommandRequest
import discord4j.discordjson.json.ImmutableApplicationCommandRequest
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import spkchan.application.usecases.ApplicationCommandInteractionUseCase
import spkchan.application.usecases.MessageCreateUseCase
import spkchan.external.apis.rakuten.queries.FetchDailyMenuQuery

@Component
class SearchRecipeUseCase(
    private val fetchDailyMenuQuery: FetchDailyMenuQuery
) : ApplicationCommandInteractionUseCase {

    companion object {
        private const val IDENTIFIER = "3cook"
    }

    override val commandName = IDENTIFIER

    override val commandRequest: ImmutableApplicationCommandRequest
        get() = ApplicationCommandRequest.builder()
            .name(commandName)
            .description("higawari oryouri oshiete kureru.")
//            .addOption(
//                ApplicationCommandOptionData.builder()
//                    .name("digits")
//                    .description("Number of digits (1-20)")
//                    .type(ApplicationCommandOption.Type.INTEGER.value)
//                    .required(false)
//                    .build()
//            )
            .build()

    override fun handle(event: ApplicationCommandInteractionEvent): Mono<Void> {
        val recipeRanking = fetchDailyMenuQuery.handle().recipes.joinToString("\n\n") {
            """
${it.rank}位 ${it.title}
【材料】${it.ingredientsDescription.replace(Regex("[\r\n]]"), " ")}
【説明】${it.recipeDescription}
${it.recipeUrl}
""".trimIndent()
        }

        return event.reply(
            """
今日の献立ランキングです

$recipeRanking
            """.trimIndent()
        )
    }
}