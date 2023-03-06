package spkchan.external.apis.zaim

import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.builder.api.DefaultApi10a
import com.github.scribejava.core.model.OAuth1RequestToken
import com.github.scribejava.core.oauth.OAuth10aService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Component
class ZaimApiClient(
    private val zaimOAUthClient: OAuth10aService,
) {
    fun fetchRequestToken(): OAuth1RequestToken {
        return zaimOAUthClient.requestToken
    }

    fun generateAuthorizationUrl(requestToken: OAuth1RequestToken): String {
        return zaimOAUthClient.getAuthorizationUrl(requestToken)
    }
}

@Configuration
class ZaimApiClientConfig(
    @Value("\${botconfig.zaim.oauth.consumer_key}") private val consumerKey: String,
    @Value("\${botconfig.zaim.oauth.consumer_secret}") private val consumerSecret: String,
) {
    companion object {
        private const val API_BASE_URL = "https://api.zaim.net/v2"
    }

    @Bean
    fun zaimOAUthClient(): OAuth10aService = ServiceBuilder(consumerKey).apiSecret(consumerSecret)
        // Zaimのcallbackのjsがバグっててリダイレクトされないので設定しない
        // .callback("http://localhost:8000/callback/zaim/oauth/access_token")
        .build(object : DefaultApi10a() {
            override fun getRequestTokenEndpoint() = "$API_BASE_URL/auth/request"
            override fun getAccessTokenEndpoint() = "$API_BASE_URL/auth/access"
            override fun getAuthorizationBaseUrl() = "https://auth.zaim.net/users/auth"
        })
}
