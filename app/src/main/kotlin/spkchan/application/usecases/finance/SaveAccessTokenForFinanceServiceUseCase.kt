package spkchan.application.usecases.finance

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import spkchan.application.usecases.WebRequestUseCase
import spkchan.external.apis.zaim.ZaimApiClient

@Component
class SaveAccessTokenForFinanceServiceUseCase(
    private val zaimApiClient: ZaimApiClient,
) : WebRequestUseCase<SaveAccessTokenForFinanceServiceUseCase.Request, SaveAccessTokenForFinanceServiceUseCase.Response> {

    override fun handle(request: Request): Response {
        // tokenとsecret 取得
        // val tokenResult = zaimApiClient.getAuthenticationToken(request.oauthToken, request.oauthVerifier)

        // TODO: pre authを検索してuserを確定させる
        // TODO: DBに保存、token と accountを紐づける

        // TODO: Discordのwebhookを叩いてチャット飛ばす
        TODO("Not yet implemented")
    }

    data class Request(
        val oauthToken: String,
        val oauthVerifier: String,
    ) : WebRequestUseCase.Request
    data class Response(override val status: HttpStatus) : WebRequestUseCase.Response
}
