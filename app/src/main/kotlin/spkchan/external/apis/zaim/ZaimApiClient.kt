package spkchan.external.apis.zaim

import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.builder.api.DefaultApi10a
import com.github.scribejava.core.model.OAuth1AccessToken
import com.github.scribejava.core.model.OAuth1RequestToken
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Response
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.oauth.OAuth10aService
import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import spkchan.domain.models.finance.FinancialRecord
import spkchan.domain.models.finance.Income
import spkchan.domain.models.finance.IncomeCategory
import spkchan.domain.models.finance.Payment
import spkchan.domain.models.finance.PaymentCategory
import spkchan.domain.models.finance.subGenres
import spkchan.domain.models.finance.valueOf
import spkchan.external.apis.toMap
import spkchan.external.apis.zaim.definitions.ZaimFetchRecordRequest
import spkchan.external.apis.zaim.definitions.ZaimFetchRecordResponse
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

@Component
class ZaimApiClient(
    private val zaimOAUthClient: OAuth10aService,
    private val gson: Gson,
) {
    companion object {
        const val API_BASE_URL = "https://api.zaim.net/v2"
    }

    fun fetchRequestToken(): OAuth1RequestToken {
        return zaimOAUthClient.requestToken
    }

    fun generateAuthorizationUrl(requestToken: OAuth1RequestToken): String {
        return zaimOAUthClient.getAuthorizationUrl(requestToken)
    }

    fun fetchAccessToken(requestToken: OAuth1RequestToken, oauthVerifier: String): OAuth1AccessToken {
        return zaimOAUthClient.getAccessToken(requestToken, oauthVerifier)
    }

    fun verifyUser(accessToken: OAuth1AccessToken): Boolean {
        val response = zaimOAUthClient.executeZaimRequest(Verb.GET, accessToken, "$API_BASE_URL/home/user/verify")
        return response.isSuccessful
        // ユーザー情報が欲しかったら以下で取得する
        // gson.fromJson(response.body, ZaimVerifyUserResponse::class.java)
    }

    private fun fetchRecord(accessToken: OAuth1AccessToken, request: ZaimFetchRecordRequest): ZaimFetchRecordResponse {
        val response = zaimOAUthClient.executeZaimRequest(
            Verb.GET,
            accessToken,
            "$API_BASE_URL/home/money",
        ) {
            request.toMap().forEach { (key, any) ->
                if (any is Collection<*>) {
                    any.forEach { item ->
                        it.addQuerystringParameter(key, item.toString())
                    }
                } else {
                    it.addQuerystringParameter(key, any.toString())
                }
            }
        }
        return gson.fromJson(response.body, ZaimFetchRecordResponse::class.java)
    }

    fun fetchIncome(accessToken: OAuth1AccessToken): ZaimFetchRecordResponse {
        return fetchRecord(accessToken, ZaimFetchRecordRequest(category_id = IncomeCategory.Salary.id))
    }

    fun fetchPayment(accessToken: OAuth1AccessToken): List<Payment> {
        val response = fetchRecord(accessToken, ZaimFetchRecordRequest(category_id = PaymentCategory.Food.id))
        val result = response.money.map {
            val category = PaymentCategory.valueOf(it.categoryId)
            Payment(
                recordId = it.id,
                transactionDateTime = LocalDateTime.parse(it.date, DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")),
                amount = it.amount,
                place = it.place,
                comment = it.comment,
                category = category,
                subGenre = category.subGenres().valueOf(it.genreId),
            )
        }
        return result
    }

    fun fetchWeeklyBalance(accessToken: OAuth1AccessToken): List<FinancialRecord> {
        val lastMonday = LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.MONDAY))
        val lastSunday = lastMonday.plusWeeks(1).minusDays(1)
        val response = fetchRecord(
            accessToken,
            ZaimFetchRecordRequest(
                start_date = lastMonday.format(DateTimeFormatter.ISO_DATE),
                end_date = lastSunday.format(DateTimeFormatter.ISO_DATE),
                limit = 100,
            ),
        )
        val result = response.money.map {
            if (it.mode == ZaimFetchRecordResponse.Money.MODE_INCOME) {
                Income(
                    recordId = it.id,
                    transactionDateTime = LocalDateTime.parse(it.date, DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")),
                    amount = it.amount,
                    place = it.place,
                    comment = it.comment,
                    category = IncomeCategory.valueOf(it.id),
                )
            } else {
                val category = PaymentCategory.valueOf(it.categoryId)
                Payment(
                    recordId = it.id,
                    transactionDateTime = LocalDateTime.parse(it.date, DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")),
                    amount = it.amount,
                    place = it.place,
                    comment = it.comment,
                    category = category,
                    subGenre = category.subGenres().valueOf(it.genreId),
                )
            }
        }
        return result
    }
}

@Configuration
class ZaimApiClientConfig(
    @Value("\${botconfig.zaim.oauth.consumer_key}") private val consumerKey: String,
    @Value("\${botconfig.zaim.oauth.consumer_secret}") private val consumerSecret: String,
) {

    @Bean
    fun zaimOAUthClient(): OAuth10aService = ServiceBuilder(consumerKey).apiSecret(consumerSecret)
        // Zaimのcallbackのjsがバグっててリダイレクトされないので設定しない
        // .callback("http://localhost:8000/callback/zaim/oauth/access_token")
        .build(object : DefaultApi10a() {
            override fun getRequestTokenEndpoint() = "${ZaimApiClient.API_BASE_URL}/auth/request"
            override fun getAccessTokenEndpoint() = "${ZaimApiClient.API_BASE_URL}/auth/access"
            override fun getAuthorizationBaseUrl() = "https://auth.zaim.net/users/auth"
        })
}

private fun OAuth10aService.executeZaimRequest(
    verb: Verb,
    accessToken: OAuth1AccessToken,
    url: String,
    setting: (OAuthRequest) -> Unit = {},
): Response {
    val request = OAuthRequest(verb, url)
        .apply(setting)
        .apply {
            addHeader("Content-Type", "application/json")
        }.apply { signRequest(accessToken, this) }
    return execute(request)
}
