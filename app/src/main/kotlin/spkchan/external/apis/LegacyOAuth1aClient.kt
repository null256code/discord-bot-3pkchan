package spkchan.external.apis

import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestTemplate
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class LegacyOAuth1aClient(
    private val consumerKey: String,
    private val consumerSecret: String,
    private val requestTokenUrl: String,
    private val authorizeUrl: String,
    private val accessTokenUrl: String,
    private val restTemplate: RestTemplate,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(LegacyOAuth1aClient::class.java)
        private const val SIGNATURE_ALGORITHM = "HmacSHA1"
    }

    private fun generateSignature(method: HttpMethod, url: String, parameterMap: Map<String, String>): String {
        val urlEncode = { str: String -> URLEncoder.encode(str, StandardCharsets.UTF_8) }

        val parameter = parameterMap.map { (key, value) -> "$key=$value" }.joinToString("&")
        // TODO: keyの生成、ドキュメント確認する
        val key = "${urlEncode(consumerKey)}&${urlEncode(consumerSecret)}"
        val text = "${method.name}&${urlEncode(url)}&${urlEncode(parameter)}"
        return Mac.getInstance(SIGNATURE_ALGORITHM).run {
            init(SecretKeySpec(key.toByteArray(), SIGNATURE_ALGORITHM))
            Base64.getEncoder().encodeToString(doFinal(text.toByteArray()))
        }
    }

    private fun Map<String, String>.addSignature(method: HttpMethod, url: String) =
        toSortedMap().let { it + mapOf("oauth_signature" to generateSignature(method, url, it)) }.toSortedMap()

    fun getPreAuthenticationToken(callbackUrl: String): GetPreAuthenticationTokenResponse? {
        val body = "oauth_callback=$callbackUrl"
        val bodyHash = MessageDigest.getInstance("SHA-1").digest(body.toByteArray())
        val bodyHashParameter = Base64.getEncoder().encodeToString(bodyHash).let { URLEncoder.encode(it, StandardCharsets.UTF_8) }

        val oauthHeader = mapOf(
            "oauth_body_hash" to bodyHashParameter,
            "oauth_consumer_key" to consumerKey,
            // "oauth_consumer_secret" to consumerSecret,
            // "oauth_callback" to callbackUrl,
            "oauth_signature_method" to "HMAC-SHA1",
            "oauth_version" to "1.0",
            "oauth_nonce" to UUID.randomUUID().toString(),
            "oauth_timestamp" to ZonedDateTime.now(ZoneId.of("Asia/Tokyo")).toEpochSecond().toString(),
        ).addSignature(HttpMethod.POST, requestTokenUrl)

        val request = RequestEntity
            .post(requestTokenUrl)
            .headers {
                it.add(
                    HttpHeaders.AUTHORIZATION,
                    "OAuth ${oauthHeader.map { (key, value) -> "$key=\"$value\"" }.joinToString(",")}",
                )
            }
            .body(body)
        val result = kotlin.runCatching { restTemplate.exchange(request, GetPreAuthenticationTokenResponse::class.java) }

        return when {
            result.isSuccess -> {
                logger.info("getPreAuthenticationToken() is finished.")
                result.getOrNull()?.body?.let { GetPreAuthenticationTokenResponse(it.oauthToken, it.oauthTokenSecret) }
            }
            else -> {
                logger.warn("getPreAuthenticationToken() is failed.")
                logger.warn(result.toString())
                null // TODO: Responseの結果型を作るべき
            }
        }
    }
    data class GetPreAuthenticationTokenResponse(val oauthToken: String, val oauthTokenSecret: String)

    fun generateAuthorizeUrl(oauthToken: String) = "$authorizeUrl?oauth_token=$oauthToken"

    fun getAuthenticationToken(oauthToken: String, oauthVerifier: String) {
        val oauthHeader = mapOf(
            "oauth_consumer_key" to consumerKey,
            "oauth_consumer_secret" to consumerSecret,
            "oauth_signature_method" to "HMAC-SHA1",
            "oauth_token" to oauthToken,
            "oauth_verifier" to oauthVerifier,
            "oauth_version" to "1.0",
        )
        val request = RequestEntity
            .post(accessTokenUrl)
            .headers {
                it.add(
                    HttpHeaders.AUTHORIZATION,
                    "OAuth ${oauthHeader.map { (key, value) -> "$key=\"$value\"" }.joinToString(",")}",
                )
            }.build()
        // val result = kotlin.runCatching { restTemplate.exchange(request, GetAuthenticationTokenResponse::class.java) }
    }
}
