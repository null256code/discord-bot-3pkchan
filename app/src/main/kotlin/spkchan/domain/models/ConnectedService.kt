package spkchan.domain.models

import com.github.scribejava.core.model.OAuth1AccessToken

// TODO: 接続方法がoauth1しかない前提になってしまっている。他の接続方法増えたときに直す
open class AuthenticatedService(
    val accountConnectedServiceId: Long,
    val serviceType: ExternalService,
    val accessToken: OAuth1AccessToken,
)

class UnsavedAuthenticatedService(
    serviceType: ExternalService,
    accessToken: OAuth1AccessToken,
) : AuthenticatedService(0L, serviceType, accessToken)

enum class ExternalService {
    Zaim,
}
