package spkchan.adapter.controllers.callback

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import spkchan.application.usecases.finance.SaveAccessTokenForFinanceServiceUseCase

@RestController
@RequestMapping("callback/zaim")
class ZaimCallbackController(
    private val saveAccessTokenForFinanceServiceUseCase: SaveAccessTokenForFinanceServiceUseCase,
) {

    @GetMapping("oauth/access_token")
    fun callbackByOauth(request: CallbackByOauthRequest): HttpStatus {
        val response = saveAccessTokenForFinanceServiceUseCase.handle(
            SaveAccessTokenForFinanceServiceUseCase.Request(
                oauthToken = request.oauthToken,
                oauthVerifier = request.oauthVerifier,
            ),
        )
        return response.status
    }

    data class CallbackByOauthRequest(val oauthToken: String, val oauthVerifier: String)
}
