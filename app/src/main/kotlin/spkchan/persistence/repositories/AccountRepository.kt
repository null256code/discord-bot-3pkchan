package spkchan.persistence.repositories

import nu.studer.jooq.spkchan.Tables.DISCORD_ACCOUNT
import nu.studer.jooq.spkchan.tables.Account.ACCOUNT
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import spkchan.domain.models.DiscordAccount
import spkchan.domain.models.UnsavedDiscordAccount

@Repository
class AccountRepository(
    private val dslContext: DSLContext,
) {
    fun findAccount(id: Long): DiscordAccount? {
        return dslContext
            .select(
                ACCOUNT.ACCOUNT_ID,
                DISCORD_ACCOUNT.DISCORD_ACCOUNT_ID,
                DISCORD_ACCOUNT.USER_NAME,
                DISCORD_ACCOUNT.DISCRIMINATOR,
            )
            .from(ACCOUNT)
            .join(DISCORD_ACCOUNT).using(ACCOUNT.ACCOUNT_ID)
            .where(DISCORD_ACCOUNT.DISCORD_ACCOUNT_ID.eq(id))
            .fetchOne {
                DiscordAccount(it.value1(), it.value2(), it.value3(), it.value4())
            }
    }

    fun saveAccount(account: UnsavedDiscordAccount): DiscordAccount {
        val accountId = dslContext.insertInto(ACCOUNT).defaultValues()
            .returningResult(ACCOUNT.ACCOUNT_ID)
            .fetchSingle().value1()
        dslContext.insertInto(
            DISCORD_ACCOUNT,
            DISCORD_ACCOUNT.DISCORD_ACCOUNT_ID,
            DISCORD_ACCOUNT.ACCOUNT_ID,
            DISCORD_ACCOUNT.USER_NAME,
            DISCORD_ACCOUNT.DISCRIMINATOR,
        ).values(account.discordAccountId, accountId, account.userName, account.discriminator)
            .execute()
        return DiscordAccount(accountId, account)
    }
}
