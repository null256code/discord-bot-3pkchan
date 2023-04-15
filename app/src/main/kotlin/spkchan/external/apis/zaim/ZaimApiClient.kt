package spkchan.external.apis.zaim

import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.builder.api.DefaultApi10a
import com.github.scribejava.core.model.OAuth1AccessToken
import com.github.scribejava.core.model.OAuth1RequestToken
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.oauth.OAuth10aService
import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

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
        val request = OAuthRequest(Verb.GET, "$API_BASE_URL/home/user/verify")
        request.addHeader("Content-Type", "application/json")
        zaimOAUthClient.signRequest(accessToken, request)
        val response = zaimOAUthClient.execute(request)
        return response.isSuccessful
        // ユーザー情報が欲しかったら以下で取得する
        // gson.fromJson(response.body, ZaimVerifyUserResponse::class.java)
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

data class ZaimVerifyUserResponse(
    val me: Me,
    val requested: Long,
) {
    data class Me(
        val login: String,
        val inputCount: Int,
        val dayCount: Int,
        val repeatCount: Int,
        val id: Long,
        val currencyCode: String,
        val week: Int,
        val month: Int,
        val active: Int,
        val day: Int,
        val profileModified: String,
        val name: String,
        val created: String,
        val profileImageUrl: String,
        val coverImageUrl: String,
    )
}
