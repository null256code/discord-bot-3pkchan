package spkchan.persistence.repositories

import com.github.scribejava.core.model.OAuth1AccessToken
import com.github.scribejava.core.model.OAuth1RequestToken
import nu.studer.jooq.spkchan.Tables.ACCOUNT_CONNECTED_SERVICE
import nu.studer.jooq.spkchan.Tables.ACCOUNT_TOKEN_OAUTH1A
import nu.studer.jooq.spkchan.Tables.REQUEST_AUTHENTICATION_OAUTH1A
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import spkchan.domain.models.Account
import spkchan.domain.models.AuthenticatedService
import spkchan.domain.models.ExternalService
import spkchan.domain.models.UnsavedAuthenticatedService
import java.time.LocalDateTime

@Repository
class ConnectedServiceRepository(
    private val dslContext: DSLContext,
) {
    fun saveRequestToken(account: Account, service: ExternalService, requestToken: OAuth1RequestToken) {
        dslContext.insertInto(
            REQUEST_AUTHENTICATION_OAUTH1A,
            REQUEST_AUTHENTICATION_OAUTH1A.ACCOUNT_ID,
            REQUEST_AUTHENTICATION_OAUTH1A.SERVICE_NAME,
            REQUEST_AUTHENTICATION_OAUTH1A.REQUEST_AUTHENTICATION_TIME,
            REQUEST_AUTHENTICATION_OAUTH1A.REQUEST_TOKEN,
            REQUEST_AUTHENTICATION_OAUTH1A.REQUEST_TOKEN_SECRET,
        )
            .values(account.accountId, service.name, LocalDateTime.now(), requestToken.token, requestToken.tokenSecret)
            .execute()
    }

    fun findRequestToken(account: Account, service: ExternalService): OAuth1RequestToken? {
        return dslContext
            .select(
                REQUEST_AUTHENTICATION_OAUTH1A.REQUEST_TOKEN,
                REQUEST_AUTHENTICATION_OAUTH1A.REQUEST_TOKEN_SECRET,
            )
            .from(REQUEST_AUTHENTICATION_OAUTH1A)
            .where(REQUEST_AUTHENTICATION_OAUTH1A.ACCOUNT_ID.eq(account.accountId))
            .and(REQUEST_AUTHENTICATION_OAUTH1A.SERVICE_NAME.eq(service.name))
            .orderBy(REQUEST_AUTHENTICATION_OAUTH1A.REQUEST_AUTHENTICATION_OAUTH1A_ID.desc())
            .limit(1)
            .fetchOne {
                OAuth1RequestToken(it.value1(), it.value2())
            }
    }

    fun saveAccessToken(account: Account, service: AuthenticatedService) {
        val isExists = findConnectedServices(account).any { it.serviceType == service.serviceType }
        if (isExists) {
            dslContext
                .update(ACCOUNT_TOKEN_OAUTH1A)
                .set(ACCOUNT_TOKEN_OAUTH1A.ACCESS_TOKEN, service.accessToken.token)
                .set(ACCOUNT_TOKEN_OAUTH1A.ACCESS_TOKEN_SECRET, service.accessToken.tokenSecret)
                .where(ACCOUNT_TOKEN_OAUTH1A.ACCOUNT_CONNECTED_SERVICE_ID.eq(service.accountConnectedServiceId))
                .execute()
        } else {
            require(service is UnsavedAuthenticatedService)
            dslContext.transaction { tx ->
                val accountConnectedServiceId = tx.dsl()
                    .insertInto(
                        ACCOUNT_CONNECTED_SERVICE,
                        ACCOUNT_CONNECTED_SERVICE.ACCOUNT_ID,
                        ACCOUNT_CONNECTED_SERVICE.SERVICE_NAME,
                    )
                    .values(account.accountId, ExternalService.Zaim.name)
                    .returningResult(ACCOUNT_CONNECTED_SERVICE.ACCOUNT_CONNECTED_SERVICE_ID)
                    .fetchSingle().value1()

                tx.dsl().insertInto(
                    ACCOUNT_TOKEN_OAUTH1A,
                    ACCOUNT_TOKEN_OAUTH1A.ACCOUNT_CONNECTED_SERVICE_ID,
                    ACCOUNT_TOKEN_OAUTH1A.ACCESS_TOKEN,
                    ACCOUNT_TOKEN_OAUTH1A.ACCESS_TOKEN_SECRET,
                ).values(accountConnectedServiceId, service.accessToken.token, service.accessToken.tokenSecret)
                    .execute()
            }
        }
    }

    fun findConnectedServices(account: Account): List<AuthenticatedService> {
        return dslContext
            .select(
                ACCOUNT_CONNECTED_SERVICE.ACCOUNT_CONNECTED_SERVICE_ID,
                ACCOUNT_CONNECTED_SERVICE.SERVICE_NAME,
                ACCOUNT_TOKEN_OAUTH1A.ACCESS_TOKEN,
                ACCOUNT_TOKEN_OAUTH1A.ACCESS_TOKEN_SECRET,
            )
            .from(ACCOUNT_CONNECTED_SERVICE)
            .join(ACCOUNT_TOKEN_OAUTH1A).using(ACCOUNT_CONNECTED_SERVICE.ACCOUNT_CONNECTED_SERVICE_ID)
            .where(ACCOUNT_CONNECTED_SERVICE.ACCOUNT_ID.eq(account.accountId))
            .fetch {
                AuthenticatedService(it.value1(), ExternalService.valueOf(it.value2()), OAuth1AccessToken(it.value3(), it.value4()))
            }
    }
}
