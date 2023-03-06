package spkchan.domain.models

class DiscordAccount(
    accountId: Long,
    val discordAccountId: Long,
    val userName: String,
    val discriminator: String,
) : Account(accountId) {
    constructor(
        accountId: Long,
        unsavedDiscordAccount: UnsavedDiscordAccount,
    ) : this(
        accountId,
        unsavedDiscordAccount.discordAccountId,
        unsavedDiscordAccount.userName,
        unsavedDiscordAccount.discriminator,
    )
}

data class UnsavedDiscordAccount(
    val discordAccountId: Long,
    val userName: String,
    val discriminator: String,
)
