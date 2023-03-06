package spkchan.persistence.repositories

import nu.studer.jooq.spkchan.Tables.REQUEST_AUTHENTICATION_OAUTH1A
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import spkchan.domain.models.Account
import java.time.LocalDateTime

@Repository
class ConnectedServiceRepository(
    private val dslContext: DSLContext,
) {
    fun saveRequestToken(account: Account, serviceName: String, requestToken: String) {
        dslContext.insertInto(
            REQUEST_AUTHENTICATION_OAUTH1A,
            REQUEST_AUTHENTICATION_OAUTH1A.ACCOUNT_ID,
            REQUEST_AUTHENTICATION_OAUTH1A.SERVICE_NAME,
            REQUEST_AUTHENTICATION_OAUTH1A.REQUEST_AUTHENTICATION_TIME,
            REQUEST_AUTHENTICATION_OAUTH1A.OAUTH_REQUEST_TOKEN,
        )
            .values(account.accountId, serviceName, LocalDateTime.now(), requestToken)
            .execute()
    }
}
