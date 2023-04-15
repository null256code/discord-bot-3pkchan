package spkchan.adapter.listeners

import discord4j.core.`object`.command.ApplicationCommandOption
import discord4j.discordjson.json.ApplicationCommandOptionData
import discord4j.discordjson.json.ApplicationCommandRequest
import discord4j.discordjson.json.ImmutableApplicationCommandOptionData
import discord4j.discordjson.json.ImmutableApplicationCommandRequest

sealed class SubCommand(val name: String) {
    abstract val option: ImmutableApplicationCommandOptionData
}

sealed class MainCommand(val name: String) {
    abstract val subCommands: List<SubCommand>
    abstract val command: ImmutableApplicationCommandRequest

    companion object {
        fun values() = MainCommand::class.sealedSubclasses.map { it.objectInstance!! }
    }

    object Cook : MainCommand("3cook") {
        override val subCommands = emptyList<SubCommand>()
        override val command = ApplicationCommandRequest.builder()
            .name(name)
            .description("日替わりのレシピランキングを取得します")
            .build()
    }

    object Kakeibo : MainCommand("3kakeibo") {
        override val subCommands = listOf(SignIn, VerifyUser)
        override val command = ApplicationCommandRequest.builder()
            .name(name)
            .description("zaim de iroiro dekiru.")
            .addAllOptions(subCommands.map { it.option })
            .build()

        object SignIn : SubCommand("signin") {
            override val option = ApplicationCommandOptionData.builder()
                .name(name)
                .description("ZaimとDiscordのアカウントを紐づけます。他の機能を使う前に行う必要があります。")
                .type(ApplicationCommandOption.Type.SUB_COMMAND.value)
                .required(false)
                .build()
        }

        object VerifyUser : SubCommand("verify") {
            override val option = ApplicationCommandOptionData.builder()
                .name(name)
                .description("Zaimと認証できているかをチェックします。")
                .type(ApplicationCommandOption.Type.SUB_COMMAND.value)
                .required(false)
                .build()
        }
    }
}
