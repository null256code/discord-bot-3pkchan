package spkchan.application.usecases

import org.springframework.http.HttpStatus

interface WebRequestUseCase<Req : WebRequestUseCase.Request, Res : WebRequestUseCase.Response> {

    fun handle(request: Req): Res

    interface Request
    interface Response {
        val status: HttpStatus
    }
}
