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
            .description("higawari oryouri oshiete kureru.")
            .build()
    }

    object Kakeibo : MainCommand("3kakeibo") {
        override val subCommands = listOf(SignUp)
        override val command = ApplicationCommandRequest.builder()
            .name(name)
            .description("zaim de iroiro dekiru.")
            .addAllOptions(subCommands.map { it.option })
            .build()

        object SignUp : SubCommand("signup") {
            override val option = ApplicationCommandOptionData.builder()
                .name(name)
                .description("ZaimとDiscordのアカウントを紐づけます。他の機能を使う前に行う必要があります。")
                .type(ApplicationCommandOption.Type.SUB_COMMAND.value)
                .required(false)
                .build()
        }
    }
}
